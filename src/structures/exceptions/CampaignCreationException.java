package structures.exceptions;

/**
 * Exception thrown when a campaign object could not be created.
 * @author Enrique Areyan Viqueira
 */
@SuppressWarnings("serial")
public class CampaignCreationException extends Exception {

  /**
   * Constructor.
   * @param info - exception information.
   */
  public CampaignCreationException(String info) {
    super(info);
  }
  
}
