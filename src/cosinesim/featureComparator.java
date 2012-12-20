package cosinesim;
import java.util.*;

public class featureComparator implements Comparator {


	public int compare(Object obj1, Object obj2) {
		 feature a = (feature) obj1;
		 feature b = (feature) obj2;
		 //returns the reverse of the natural order
		 if(a.getWeight() > b.getWeight())  return -1;
		 if(a.getWeight() < b.getWeight())  return 1;
		 return 0;


	}
	public boolean equals(Object obj1, Object obj2) {
		 feature a = (feature) obj1;
		 feature b = (feature) obj2;
		 if(a.word.equals(b.word) )
			 return true;
		 return false;
	}

}