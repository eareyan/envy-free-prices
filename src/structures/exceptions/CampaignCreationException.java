package structures.exceptions;

@SuppressWarnings("serial")
public class CampaignCreationException extends Exception{

	public CampaignCreationException(String info){
		super(info);
	}
}
