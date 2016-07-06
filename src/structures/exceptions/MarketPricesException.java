package structures.exceptions;

@SuppressWarnings("serial")
public class MarketPricesException extends Exception{
	
	public MarketPricesException(String info){
		super(info);
	}
}
