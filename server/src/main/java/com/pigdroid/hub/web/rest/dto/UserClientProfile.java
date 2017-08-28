package com.pigdroid.hub.web.rest.dto;

import com.pigdroid.social.model.user.User;

public class UserClientProfile extends ClientProfile {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String firstName;
	private String lastName;
	private String email;
	private String imageUrl;
	private Boolean reverseAccepted;
	private Boolean accepted;

	public static class Builder {
		
		private User user;
		private boolean reverseAccepted = false;
		private boolean accepted = false;
		
		private Builder() {
			
		}

		public Builder from(User user) {
			this.user = user;
			return this;
		}
		
		public UserClientProfile build() {
			UserClientProfile profile = new UserClientProfile();
			profile.setFirstName(user.getFirstName());
			profile.setLastName(user.getLastName());
			profile.setEmail(user.getEmail());
			profile.setReverseAccepted(reverseAccepted);
			profile.setAccepted(accepted);
			//TODO set games??? Usr usr = user.getUsr();
			//TODO set imageUrl
			return profile;
		}
		
		public Builder reverseAccepted(boolean accepted) {
			this.reverseAccepted  = accepted;
			return this;
		}
		
		public Builder accepted(boolean accepted) {
			this.accepted = accepted;
			return this;
		}
		
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setReverseAccepted(Boolean accepted) {
		this.reverseAccepted = accepted;
	}
	
	public boolean isReverseAccepted() {
		return reverseAccepted != null ? reverseAccepted : false;
	}

	public boolean isAccepted() {
		return accepted != null ? accepted : false;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public String getType() {
		return "user";
	}

}
