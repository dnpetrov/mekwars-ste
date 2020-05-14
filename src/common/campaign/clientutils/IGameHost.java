package common.campaign.clientutils;

public interface IGameHost {
	public void changeStatus(int newStatus);
    public boolean isAdmin();
    public boolean isMod();
    public String getUsername();
}
