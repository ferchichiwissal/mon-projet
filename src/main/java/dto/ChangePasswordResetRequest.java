package dto;

 
	 
	 

	public record ChangePasswordResetRequest(
	        String newPassword,
	        String confirmationPassword
	) {
	}