package christopher.schoening.HandRefiller.Utils;

/**
 * 
 * @author maerics
 * @see https://stackoverflow.com/a/2671052
 *
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> { 
	
	  public final X Key; 
	  public final Y Value; 
	  
	  public Tuple(X x, Y y) { 
	    this.Key = x; 
	    this.Value = y; 
	  } 
	  
	} 
