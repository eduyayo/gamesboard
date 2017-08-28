package com.pigdroid.gameboard.view.detail.game.tile;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.couchbase.lite.Document;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pigdroid.android.hateaidl.HateAIDLConnection;
import com.pigdroid.game.GameControllerProvider;
import com.pigdroid.game.board.tile.controller.TileBoardGameController;
import com.pigdroid.game.board.tile.model.TileBoardGameModel;
import com.pigdroid.game.board.tile.model.TileBoardGameSelection;
import com.pigdroid.game.board.tile.model.UITileBoardGameContext;
import com.pigdroid.game.controller.GameController.GameControllerListener;
import com.pigdroid.game.controller.GameController.GameUIControllerListener;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.turn.controller.TurnGameController.TurnGameControllerListener;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.service.RestService;
import com.pigdroid.gameboard.util.DocumentUtils;
import com.pigdroid.gameboard.util.JSONUtils;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.gameboard.util.StringUtils;
import com.pigdroid.gameboard.util.UnitUtils;
import com.pigdroid.gameboard.view.DrawerActivity;
import com.pigdroid.gameboard.view.PlayerViewUtils;
import com.pigdroid.gameboard.view.detail.game.GameDetailFragment;
import com.pigdroid.gameboard.view.detail.game.WithdrawGameDialogFragment;
import com.pigdroid.gfx.android.SquareSurfaceView;
import com.pigdroid.gfx.android.SquareSurfaceView.SquareSurfaceViewListener;
import com.pigdroid.hub.model.message.GameMessage;
import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.message.HubMessage.Builder;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class TileBoardGameFragmentController implements GameControllerListener, GameUIControllerListener, SquareSurfaceViewListener, TurnGameControllerListener {

	private RestService restService;
    private HateAIDLConnection<RestService> restServiceConnection;
    private Matrix coordTransformMatrix;
    private Matrix bitmapTransformMatrix;
    private int rotationDegreesPerPlayer = 180;

    public TileBoardGameFragmentController() {
        throw new RuntimeException("CONSTRUCTOR NEVER TO BE USED!"); //
	}

	private static final long TIME_TO_WAIT_ON_AUTO = 500;
	
	private TileBoardGameController<?, ?, ?> controller;
	private Map<Long, Bitmap> bitmaps = Collections.synchronizedMap(new HashMap<Long, Bitmap>());
    private Map<Integer, Bitmap> tiles = new HashMap<Integer, Bitmap>();
	
    private int size;
    private int firstTileOffset;
	private float tileWidth;
	private int tileWidthInt;
	private Map<Integer, GameSelection> selectables;
	private List<GameSelection> selections;

	private int prevClickTileX = -1;
	private int prevClickTileY = -1;

	private BootstrapButton undoButton;
	private BootstrapButton sendButton;
	private BootstrapButton tieButton;
	private BootstrapButton withdrawButton;
	
	private Document document;
	private GameDetailFragment owner;

	private List<GameSelection> selectionsToSend;

	public TileBoardGameFragmentController(GameDetailFragment gameDetailFragment) {
		this.owner = gameDetailFragment;

		Thread.currentThread().setContextClassLoader(owner.getActivity().getBaseContext().getClassLoader());

        restServiceConnection = new HateAIDLConnection<RestService>(owner.getActivity(), new HateAIDLConnection.Listener<RestService>() {
            @Override
            public void bound(RestService proxy) {
				restService = proxy;
            }
        }, RestService.class);
    }

	public void onDestroy() {
        restServiceConnection.disconnect();
		cleanBitmapCache();
		owner = null;
		undoButton = null;
		sendButton = null;
		tieButton = null;
		withdrawButton = null;
		bitmaps = null;
		controller = null;
	}
	
	private void cleanBitmapCache() {
		synchronized (bitmaps) {
			for (Bitmap bitmap : bitmaps.values()) {
				if (bitmap != null) {
					bitmap.recycle();
				}
			}
			bitmaps.clear();
		}
	}
	
	private void setEnabled(View view, boolean enabled) {
		view.setEnabled(enabled);
		if (enabled) {
			ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 1f).setDuration(500L).start();
		} else {
			ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0.0f).setDuration(500L).start();
		}
	}

	private SquareSurfaceView  getSquareSurfaceView(View view) {
		return (SquareSurfaceView) view.findViewById(R.id.surface_view);
	}

	private SquareSurfaceView  getSquareSurfaceView() {
		return getSquareSurfaceView(owner.getView());
	}

	private void initUI(View view) {
        getSquareSurfaceView(view).setSquareSurfaceViewListener(this);

		undoButton = (BootstrapButton) view.findViewById(R.id.undo);
		undoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doUndo();
			}
		});
		
		sendButton = (BootstrapButton) view.findViewById(R.id.send);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSend();
			}
		});
		
		tieButton = (BootstrapButton) view.findViewById(R.id.tie);
		tieButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doTie();
			}
		});
		
		withdrawButton = (BootstrapButton) view.findViewById(R.id.withdraw);
		withdrawButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doWithdraw();
            }
        });
	}

	private void disableButtons() {
		owner.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setEnabled(withdrawButton, false);
                setEnabled(tieButton, false);
                setEnabled(undoButton, false);
                setEnabled(sendButton, false);
            }
        });
	}
	
	private void setButtonStatus(final boolean showByConditions) {
		owner.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
				if (!showByConditions) {
					setEnabled(undoButton, false);
					setEnabled(sendButton, false);
					setEnabled(withdrawButton, false);
					setEnabled(tieButton, false);
				} else  if (controller.isCurrentTurn(PreferenceUtils.getUserEmail(owner.getActivity()))) {
                    if (controller.isTurnDone()) {
                        setEnabled(undoButton, true);
                        setEnabled(sendButton, true);
                        setEnabled(withdrawButton, false);
                        setEnabled(tieButton, false);
                    } else {
                        setEnabled(withdrawButton, true);
                        setEnabled(tieButton, true);
                        setEnabled(undoButton, false);
                        setEnabled(sendButton, false);
                    }
                } else {
                    if (controller.isTurnDone()) {
                        setEnabled(undoButton, true);
                        setEnabled(sendButton, true);
                        setEnabled(withdrawButton, false);
                        setEnabled(tieButton, false);
                    } else {
                        setEnabled(withdrawButton, false);
                        setEnabled(tieButton, false);
                        setEnabled(undoButton, false);
                        setEnabled(sendButton, false);
                    }
                }
            }
        });
	}

	private void doWithdraw() {
        WithdrawGameDialogFragment fragment = new WithdrawGameDialogFragment();
        fragment.show(owner.getFragmentManager(), "dialog");
	}

	private void doTie() {
		//TODO
	}

	private void doSend() {
		
		disableButtons();

		controller.commit();
		
		if (selectionsToSend == null || selectionsToSend.isEmpty()) {
			return;
		}
		String senderEmail = selectionsToSend.get(0).getPlayerName();
		
		GameMessage gameMessage;
		gameMessage = GameMessage.builder()
				.modelId(document.getProperty("modelId").toString())
				.payload(new JSONSerializer().deepSerialize(selectionsToSend))
				.type(GameMessage.TYPE_MSG)
				.build();
		

		final HubMessage message;
		try {
			Builder builder = HubMessage.builder()
					.from(PreferenceUtils.getUserEmail(owner.getActivity()))
					.type(HubMessage.TYPE_MSG_GAME)
					.payload(JSONUtils.createObjectMapper().writeValueAsString(gameMessage));
			for (Iterator<Player> players = controller.getPlayers(); players.hasNext(); ) {
				Player player = players.next();
				if (player instanceof HumanPlayer) {
					HumanPlayer human = (HumanPlayer) player;
					if (!senderEmail.equals(human.getEmail())) {
						builder.to(human.getEmail());
					}
				}
			}
			message = builder.build();
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				restService.sendMessage(message);
				selectionsToSend = null;
				setButtonStatus(true);
				return null;
			}
			
		}.execute();
	}

	private void doUndo() {
        getSquareSurfaceView().setClickable(false);
        disableButtons();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                    selectionsToSend = null;
                    while (controller.rollback()) {
                        try {
                            Thread.sleep(TIME_TO_WAIT_ON_AUTO);
                        } catch (InterruptedException e) {
                        }
                    }
                    controller.checkpoint();
                    setButtonStatus(true);
                    setPlayersStatus();
                    getSquareSurfaceView().setClickable(true);
                return null;
            }

        }.execute();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void onResume() {
        Thread.currentThread().setContextClassLoader(owner.getActivity().getBaseContext().getClassLoader());
        controller.loadModelFromSerialized((String) document.getProperty("saveGame"));
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				Canvas canvas = getSquareSurfaceView().lockCanvas();
				while (canvas == null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					canvas = getSquareSurfaceView().lockCanvas();
				}
                getSquareSurfaceView().unlockCanvas(canvas);
				controller.doForcePaint();
				return null;
            }

		}.execute();
		setButtonStatus(true);
	}

	private void initController() {
		this.controller = GameControllerProvider.createController("Checkers"); //TODO get it from the game
		controller.setGameControllerListener(this);
        controller.setGameUIControllerListener(this);
        controller.setTurnGameControllerListener(this);
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		initUI(view);
		initController();
	}

	@Override
	public void onGamePaint(final UIGameContext uiCtx) {
		UITileBoardGameContext uiContext = (UITileBoardGameContext) uiCtx;
		this.selectables = uiContext.getSelectables();
        this.selections = uiContext.getSelections();
        this.rotationDegreesPerPlayer = uiContext.getRotationDegreesPerPlayer();
		synchronized (bitmaps) {
			if (tileWidth == 0.0) {
				return;
			}
//			tiles = new HashMap<Integer, Bitmap>();
			Map<Integer, GameSelection> elements = uiContext.getUiElements();
			tiles.clear();
			for (Integer key : elements.keySet()) {
				GameSelection selection = elements.get(key);
				Long id = selection.getModelId();
				if (id != null) {
					Bitmap bitmap = bitmaps.get((long) id);
					if (bitmap == null) {
						bitmap = ImageUtils.createBitmap(controller.getUIResource(id), tileWidthInt, tileWidthInt);
						bitmaps.put(id, bitmap);
					}
					tiles.put(key, bitmap);
				}
			}
			if (bitmaps.get((long) TileBoardGameModel.TILE_SELECTABLE) == null) {
				bitmaps.put(
						(long) TileBoardGameModel.TILE_SELECTABLE, 
						ImageUtils.createBitmap(
                                controller.getUIResource(
                                        (long) TileBoardGameModel.TILE_SELECTABLE),
                                tileWidthInt, tileWidthInt));
			}
			if (bitmaps.get((long) TileBoardGameModel.TILE_SELECTED) == null) {
				bitmaps.put(
						(long) TileBoardGameModel.TILE_SELECTED, 
						ImageUtils.createBitmap(controller.getUIResource(
                                        (long) TileBoardGameModel.TILE_SELECTED),
                                tileWidthInt, tileWidthInt));
			}
            if (getSquareSurfaceView() != null) {
                Canvas canvas = getSquareSurfaceView().lockCanvas();
                if (canvas != null) {
                    drawCanvas(canvas);
                    getSquareSurfaceView().unlockCanvas(canvas);
                } else {
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            Canvas lockCanvas = null;
                            while (lockCanvas == null) {
                                try {
                                    Thread.sleep(100L);
                                } catch (InterruptedException e) {
                                }
                                lockCanvas = getSquareSurfaceView().lockCanvas();
                            }
                            drawCanvas(lockCanvas);
                            getSquareSurfaceView().unlockCanvas(lockCanvas);
                            return null;
                        }

                    }.execute();
                }
            }
		}
	}

	private void drawCanvas(Canvas canvas) {
		synchronized (bitmaps) {
			RectF dst = new RectF();
			canvas.drawRGB(255, 255, 255);
			int width = controller.getWidth();
			int height = controller.getHeight();
			int layerCount = controller.getLayerCount();

            //Draw the map
			for (int layer = 0; layer < layerCount; layer++) {
				for (int xi = 0; xi < width; xi++) {
					int x = getTransformedTileCoordX(xi);
					dst.left = firstTileOffset + x * tileWidth;
					for (int yi = 0; yi < height; yi++) {
						int y = getTransformedTileCoordY(yi);
						dst.top = firstTileOffset + y * tileWidth;
						dst.bottom = dst.top + tileWidth;
						dst.right = dst.left + tileWidth;
						int zindex = controller.getZIndex(xi, yi, layer);
						Bitmap bitmap = tiles.get(zindex);
						if (bitmap != null) {
                            canvas.save();
							//canvas.drawBitmap(bitmap, null, dst, null);
                            canvas.translate(dst.left, dst.top);
                            canvas.drawBitmap(bitmap, getBitmapTransformMatrix(), null);
                            canvas.restore();
						}
//						TileBoardGameSelection selection = (TileBoardGameSelection) selectables.get(zindex);
//						if (selection != null) {
//							canvas.drawBitmap(bitmaps.get((long) TileBoardGameModel.TILE_SELECTABLE), null, dst, null);
//						}
					}
				}
			}

            //Draw the selectables
            for (GameSelection selection : selectables.values()) {
                TileBoardGameSelection tileSelection = (TileBoardGameSelection) selection;
                dst.top = firstTileOffset + getTransformedTileCoordY(tileSelection.getY()) * tileWidth;
                dst.left = firstTileOffset + getTransformedTileCoordX(tileSelection.getX()) * tileWidth;
                dst.right = dst.left + tileWidth;
                dst.bottom = dst.top + tileWidth;
                canvas.drawBitmap(bitmaps.get((long) TileBoardGameModel.TILE_SELECTABLE), null, dst, null);
            }

            //Draw the selections
			for (GameSelection selection : selections) {
				TileBoardGameSelection tileSelection = (TileBoardGameSelection) selection;
				dst.top = firstTileOffset + getTransformedTileCoordY(tileSelection.getY()) * tileWidth;
				dst.left = firstTileOffset + getTransformedTileCoordX(tileSelection.getX()) * tileWidth;
				dst.right = dst.left + tileWidth;
				dst.bottom = dst.top + tileWidth;
				canvas.drawBitmap(bitmaps.get((long) TileBoardGameModel.TILE_SELECTED), null, dst, null);
			}
		}
	}

	@Override
	public void onMove(List<GameSelection> selections) {
		if (selections != null && !selections.isEmpty()) {
			String playerName = selections.get(0).getPlayerName();
			if (playerName != null) {
                play(R.raw.move);
SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(String.format("%s has made a movement.", playerName)));
			}
		}
		// TODO Animate?
		
	}

	@Override
	public void onPlayerJoined(Player player) {
        if (player != null) {
            play(R.raw.snackbar);
            SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(String.format("%s just joined the game!", player.getName())).color(R.color.bbutton_primary));
        }
		
	}

	@Override
	public void onPlayerLeft(Player player) {
        if (player != null) {
            play(R.raw.snackbar);
            SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(String.format("%s just left the game!!", player.getName())).color(R.color.bbutton_warning));
        }
	}

	@Override
	public void onStartGame() {
        play(R.raw.snackbar);
SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text("Game Started!!").color(R.color.bbutton_primary));
        DocumentUtils.setProperty(document, "gameEstate", "STARTED");
    }

	@Override
	public void onSendSelections(List<GameSelection> pSelections) {
        play(R.raw.snackbar);
SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text("Movement done"));
		this.selectionsToSend = pSelections;
	}
	
	// From Android lifecycles
	public void onPause() {
			while (controller.rollback()) {

			}
            if (document != null) {
                Map<String, Object> properties = new HashMap<String, Object>(document.getProperties());
                properties.put("saveGame", controller.getSerializedModel());
                properties.put("screenShot", getScreenShot());
				DocumentUtils.setProperties(document, properties);
            }
	}

    private byte[] getScreenShot() {
        Bitmap ret = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        drawCanvas(new Canvas(ret));
        int sSize = UnitUtils.dpToPx(owner.getActivity(), 128);
        ret = ImageUtils.resizedBitmap(ret, sSize, sSize);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ret.compress(Bitmap.CompressFormat.JPEG, 85, out);
        ret.recycle();
        return out.toByteArray();
    }

    @Override
	public boolean onIsPlayerLocal(List<GameSelection> pselections) {
		if (pselections != null && pselections.size() > 0) {
			return onIsPlayerLocal(pselections.get(0).getPlayerName());
		}
		return false;
	}

	@Override
	public boolean onIsPlayerLocal(String email) {
		return PreferenceUtils.getUserEmail(owner.getActivity()).equals(email);
	}

	@Override
	public void onChangeSize(int size) {
		if (this.size == 0 || this.size < size - tileWidthInt) {
			this.size = size;
			int prevMultiple = getPrevMultiple(size, controller.getWidth());
			tileWidth = prevMultiple / (float)controller.getWidth();
			tileWidthInt = (int) tileWidth;
            firstTileOffset = (size - prevMultiple) / 2;
			cleanBitmapCache();
			controller.doForcePaint();
		}
	}

    private int getPrevMultiple(int screenSize, int tileWidth) {
        int ret = screenSize;
        for (int i = screenSize; i > 0; i --) {
            if (i % tileWidth == 0) {
                ret = i;
                break;
            }
        }
        return ret;
    }


    @Override
	public void onTouchDown(float x, float y) {
            int tileX = (int) (x / tileWidth);
            int tileY = (int) (y / tileWidth);
            tileX = getTransformedTileCoordX(tileX);
            tileY = getTransformedTileCoordY(tileY);
            doTileClick(prevClickTileX = tileX, prevClickTileY = tileY);
	}

	private void doTileClick(int x, int y) {
		GameSelection selection = getTopMostSelection(x, y);
		if (selection != null) {
			controller.checkpoint();
            try {
                controller.select(selection);
            } catch (IllegalArgumentException e) {
                Log.w("doTileClick", "Just selected a non selectable... or something...", e);
            }
		} else {
			//Controller.rollback(); // This screws the UI because we have no control on what was selected before
		}
	}

	private int getTransformedTileCoordX(int x) {
		Matrix m = getCoordTransformMatrix();
		float [] p = new float[]{x, 0};
		m.mapPoints(p);
		return (int) p[0];
	}

	private Matrix getCoordTransformMatrix() {
		if (coordTransformMatrix == null) {
			coordTransformMatrix = new Matrix();
			int index = controller.getPlayerIndex(PreferenceUtils.getUserEmail(owner.getActivity()));
			coordTransformMatrix.setRotate(rotationDegreesPerPlayer * index);
			coordTransformMatrix.preTranslate((controller.getWidth() - 1) / -2.0f, (controller.getHeight() - 1) / -2.0f);
			coordTransformMatrix.postTranslate((controller.getWidth() - 1) / 2.0f, (controller.getHeight() - 1) / 2.0f);
		}
		return coordTransformMatrix;
	}

    private Matrix getBitmapTransformMatrix() {
        if (bitmapTransformMatrix == null) {
            bitmapTransformMatrix = new Matrix();
            int index = controller.getPlayerIndex(PreferenceUtils.getUserEmail(owner.getActivity()));
            bitmapTransformMatrix.setRotate(rotationDegreesPerPlayer * index);
            bitmapTransformMatrix.preTranslate((tileWidthInt - 1) / -2.0f, (tileWidthInt - 1) / -2.0f);
            bitmapTransformMatrix.postTranslate((tileWidthInt - 1) / 2.0f, (tileWidthInt - 1) / 2.0f);
        }
        return bitmapTransformMatrix;
    }
	
	private int getTransformedTileCoordY(int y) {
		Matrix m = getCoordTransformMatrix();
		float [] p = new float[]{0, y};
		m.mapPoints(p);
		return (int) p[1];
	}
	
	private GameSelection getTopMostSelection(int x, int y) {
		GameSelection selection = null;
		if (selectables != null && !selectables.isEmpty()) {
			for (int i = controller.getLayerCount() - 1; i > -1; i--) {
				int zindex = controller.getZIndex(x, y, i);
				selection = selectables.get(zindex);
				if (selection != null) {
					break;
				}
			}
		}
		return selection;
	}

	@Override
	public void onTouchUp(float x, float y) {
        int tileX = (int) (x / tileWidth);
        int tileY = (int) (y / tileWidth);
        tileX = getTransformedTileCoordX(tileX);
        tileY = getTransformedTileCoordY(tileY);
        if (tileX != prevClickTileX || tileY != prevClickTileY) {
            doTileClick(tileX, tileY);
        } else {
            prevClickTileX = prevClickTileY = -1;
        }
	}

	@Override
	public void onEndTurn(Player player) {
        if (player != null) {
            play(R.raw.snackbar);
            SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(String.format("%s finished turn.", player.getName())));
            if (onIsPlayerLocal(((HumanPlayer) player).getEmail())) {
                selectables = null;
                setButtonStatus(true);
            }
        }
	}

	@Override
	public void onStartTurn(Player player) {
        if (player != null) {
            play(R.raw.snackbar);
            SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(String.format("%s's turn is starting...", player.getName())));
        }
        setPlayersStatus();
	}

	public void setGame(Document document) {
		this.document = document;

		controller = GameControllerProvider.createController((String) document.getProperty("gameName"));
		controller.setGameControllerListener(this);
		controller.setGameUIControllerListener(this);
		controller.setTurnGameControllerListener(this);

        Thread.currentThread().setContextClassLoader(owner.getActivity().getBaseContext().getClassLoader());
		controller.loadModelFromSerialized((String) document.getProperty("saveGame"));
		
		String estate = (String) document.getProperty("gameEstate");
		estate = estate != null ? estate : "";
        play(R.raw.snackbar);

		switch (estate) {
		case "WAITING_PLAYERS":// Server responded or user accepted an invitation.
			SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(StringUtils.getGameEstateDesc(estate)).color(Color.YELLOW).textColor(Color.BLACK));
			setButtonStatus(false);
			break;
		case "STARTED":// All players are in and we can start or continue.
			setButtonStatus(true);
			SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(StringUtils.getGameEstateDesc(estate)).color(Color.GREEN).textColor(Color.BLACK));
			break;
		case "FINISHED":// win or lose conditions.
			SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(StringUtils.getGameEstateDesc(estate)).color(Color.YELLOW).textColor(Color.BLACK));
			break;
		default:
			SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text(StringUtils.getGameEstateDesc(estate)).color(Color.RED).textColor(Color.WHITE));
			setButtonStatus(false);
			break;
		}

        setPlayersStatus();
	}

    private void setPlayersStatus() {
        PlayerViewUtils.setPlayerStatus(owner.getActivity(), (ViewGroup) owner.getView().findViewById(R.id.player_list), controller);
    }

    private void play(int r) {
		((DrawerActivity) owner.getActivity()).play(r);
	}

	@Override
	public void onEndGame(Player currentPlayer, boolean arg0, boolean arg1, boolean arg2) {
        SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).text("End of the game!!"));
	}

	@Override
	public void onNeverReadyToStart() {
        SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).color(Color.RED).text("The game cannot continue without players..."));
        SnackbarManager.show(Snackbar.with(owner.getActivity()).position(Snackbar.SnackbarPosition.TOP).color(Color.RED).text("Game is now defunct :("));
	}

    @Override
    public void onSelect(List<GameSelection> selections) {
        play(R.raw.move);
    }

    //this method is running in UIThread
	public synchronized void processMessage(final GameMessage gameMessage) {
            switch (gameMessage.getType()) {
                case GameMessage.TYPE_JOIN:
                    controller.joinPlayer(gameMessage.getPayload());
                    break;
                case GameMessage.TYPE_REJECT:
                    controller.leavePlayer(gameMessage.getPayload());
                    break;
                case GameMessage.TYPE_MSG:
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                                List<GameSelection> gameSelections = null;
                                try {
                                    gameSelections = new JSONDeserializer<List<GameSelection>>().deserialize(gameMessage.getPayload());
                                } catch (IllegalArgumentException e) {
                                    throw new RuntimeException(e);
                                }
								try {
									for (GameSelection selection : gameSelections) {
										controller.select(selection);
										try {
											Thread.sleep(TIME_TO_WAIT_ON_AUTO);
										} catch (InterruptedException e) {
										}
									}
								} catch (IllegalArgumentException e) {
                                    Log.w("processMessage", "Just selected a non selectable... or something...", e);
								}
                                controller.commit();
                            return null;
                        }
                    }.execute();
                    break;
                default:
                    break;
            }
	}

}
