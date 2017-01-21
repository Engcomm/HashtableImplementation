import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a hashtable that using chaining for collision handling.
 * Any non-<tt>null</tt> item may be added to a hashtable.  Chains are 
 * implemented using <tt>LinkedList</tt>s.  When a hashtable is created, its 
 * initial size, maximum load factor, and (optionally) maximum chain length are 
 * specified.  The hashtable can hold arbitrarily many items and resizes itself 
 * whenever it reaches its maximum load factor or whenever it reaches its 
 * maximum chain length (if a maximum chain length has been specified).
 * 
 * Note that the hashtable allows duplicate entries.
 */
public class HashTable<T> {
	private LinkedList<T> [] table; //for an array of linked lists of <T>
	private final double loadfactor; //for the load factor of hashtable
	private int numOccupied; //for the number of positions that have been occupied
	private final int maxChainLength;//for the max length of chains
	private int currentLongestChainLength; //for the current longest chain length
	private int totalItems; //for the total count of items
    
    /**
     * Constructs an empty hashtable with the given initial size, maximum load
     * factor, and no maximum chain length.  The load factor should be a real 
     * number greater than 0.0 (not a percentage).  For example, to create a 
     * hash table with an initial size of 10 and a load factor of 0.85, one 
     * would use:
     * 
     * <dir><tt>HashTable ht = new HashTable(10, 0.85);</tt></dir>
     *
     * @param initSize the initial size of the hashtable.
     * @param loadFactor the load factor expressed as a real number.
     * @throws IllegalArgumentException if <tt>initSize</tt> is less than or 
     *         equal to 0 or if <tt>loadFactor</tt> is less than or equal to 0.0
     **/
    public HashTable(int initSize, double loadFactor) {
   	 
   	 if(initSize <= 0 || loadFactor <= 0.0) {
   		 throw new IllegalArgumentException();
   	 }
       this.table = new LinkedList[initSize];
       this.loadfactor = loadFactor;
       this.numOccupied = 0;
       this.maxChainLength = 0; //max chain length is not specified 
       this.currentLongestChainLength = 0;
       this.totalItems = 0;
    }
    
    
    /**
     * Constructs an empty hashtable with the given initial size, maximum load
     * factor, and maximum chain length.  The load factor should be a real 
     * number greater than 0.0 (and not a percentage).  For example, to create 
     * a hash table with an initial size of 10, a load factor of 0.85, and a 
     * maximum chain length of 20, one would use:
     * 
     * <dir><tt>HashTable ht = new HashTable(10, 0.85, 20);</tt></dir>
     *
     * @param initSize the initial size of the hashtable.
     * @param loadFactor the load factor expressed as a real number.
     * @param maxChainLength the maximum chain length.
     * @throws IllegalArgumentException if <tt>initSize</tt> is less than or 
     *         equal to 0 or if <tt>loadFactor</tt> is less than or equal to 0.0 
     *         or if <tt>maxChainLength</tt> is less than or equal to 0.
     **/
    public HashTable(int initSize, double loadFactor, int maxChainLength) {
   	 
   	 if(initSize <= 0 || loadFactor <= 0.0 || maxChainLength <= 0) {
   		 throw new IllegalArgumentException();
   	 }
   	 this.table = new LinkedList[initSize];
       this.loadfactor = loadFactor;
       this.numOccupied = 0;
       this.maxChainLength = maxChainLength;
       this.currentLongestChainLength = 0;
       this.totalItems = 0;
        
    }
    
    
    /**
     * Determines if the given item is in the hashtable and returns it if 
     * present.  If more than one copy of the item is in the hashtable, the 
     * first copy encountered is returned.
     *
     * @param item the item to search for in the hashtable.
     * @return the item if it is found and <tt>null</tt> if not found.
     **/
    public T lookup(T item) {
   	 int itemIndex = itemIndex(item, table.length);
   	 if(table[itemIndex] != null) {
   		 return table[itemIndex].get(0);
   	 }
        return null;
    }
    
    
    /**
     * Inserts the given item into the hashtable.  The item cannot be 
     * <tt>null</tt>.  If there is a collision, the item is added to the end of
     * the chain.
     * <p>
     * If the load factor of the hashtable after the insert would exceed 
     * (not equal) the maximum load factor (given in the constructor), then the 
     * hashtable is resized.  
     * 
     * If the maximum chain length of the hashtable after insert would exceed
     * (not equal) the maximum chain length (given in the constructor), then the
     * hashtable is resized.
     * 
     * When resizing, to make sure the size of the table is reasonable, the new 
     * size is always 2 x <i>old size</i> + 1.  For example, size 101 would 
     * become 203.  (This guarantees that it will be an odd size.)
     * </p>
     * <p>Note that duplicates <b>are</b> allowed.</p>
     *
     * @param item the item to add to the hashtable.
     * @throws NullPointerException if <tt>item</tt> is <tt>null</tt>.
     **/
    public void insert(T item) {
   	 if(item == null) {
   		 throw new NullPointerException();
   	 }
   	 
   	 int itemIndex = itemIndex(item, table.length);
   	 if(table[itemIndex] == null) {
   		 table[itemIndex] = new LinkedList<T>();
   		 numOccupied++;
   	 } 
   	 table[itemIndex].add(item);
   	 totalItems++;
		 if(table[itemIndex].size() > currentLongestChainLength) {
			 currentLongestChainLength = table[itemIndex].size();	 
		 }
		 
   	 if(needExpansion()) {
			 table = expand(table);
		 }
        
    }
    
    
    /**
     * Removes and returns the given item from the hashtable.  If the item is 
     * not in the hashtable, <tt>null</tt> is returned.  If more than one copy 
     * of the item is in the hashtable, only the first copy encountered is 
     * removed and returned.
     *
     * @param item the item to delete in the hashtable.
     * @return the removed item if it was found and <tt>null</tt> if not found.
     **/
    public T delete(T item) {
   	 if(lookup(item) == null) {
   		 return null;
   	 }
   	 
   	 int itemIndex = itemIndex(item, table.length);
   	 T toDelete = table[itemIndex].removeFirst();
   	 if(table[itemIndex].size() == 0) {
   		 numOccupied--;
   		 table[itemIndex] = null;
   	 }
   	 totalItems--;
       return toDelete;  
    }
    
    
    /**
     * Prints all the items in the hashtable to the <tt>PrintStream</tt> 
     * supplied.  The items are printed in the order determined by the index of
     * the hashtable where they are stored (starting at 0 and going to 
     * (table size - 1)).  The values at each index are printed according 
     * to the order in the <tt>LinkedList</tt> starting from the beginning. 
     *
     * @param out the place to print all the output.
     **/
    public void dump(PrintStream out) {
   	 out.println("Hashtable contents:");
   	 for (int i = 0; i < table.length; i++) {
   		 if (table[i] != null) {
   			 out.print(i + ": [");
   			 Iterator<T> itr = table[i].iterator();
   			 out.print(itr.next());
   			 while(itr.hasNext()) {
   				 out.print(", " + itr.next());
   			 }
   			 out.println("]");
   		 }
   	 }
    }
    
  
    /**
     * Prints statistics about the hashtable to the <tt>PrintStream</tt> 
     * supplied.  The statistics displayed are: 
     * <ul>
     * <li>the current table size
     * <li>the number of items currently in the table 
     * <li>the current load factor
     * <li>the length of the largest chain
     * <li>the number of chains of length 0
     * <li>the average length of the chains of length > 0
     * </ul>
     *
     * @param out the place to print all the output.
     **/
    public void displayStats(PrintStream out) {
   	 out.println("Hashtable statistics:");
   	 out.println("  current table size:       " + table.length);
       out.println("  # items in table:         " + totalItems); 
       out.println("  current load factor:      " + currentLoadFactor());
       out.println("  longest chain length:     " + currentLongestChainLength);
       out.println("  # 0-length chains:        " + (table.length - numOccupied));
       out.printf("  avg (non-0) chain length: %.2f\n", totalItems/(double)numOccupied);
    }
    
    
    private int itemIndex(T item, int hashtableSize) {

   	 if(item.hashCode() < 0) {
   		 return item.hashCode() % hashtableSize + hashtableSize;
   	 } else {
   		 return item.hashCode() % hashtableSize;
   	 }
   	 
    }
    
    private LinkedList<T> [] expand(LinkedList<T>[] original) {
   	 currentLongestChainLength = 0;
   	 numOccupied = 0;
   	 LinkedList<T> [] newtable = new LinkedList[nextPrime(original.length)];
   	 for(int i = 0; i < original.length; i++) {
   		 if(original[i] != null) {
   			 Iterator<T> itr = original[i].iterator();
   			 while (itr.hasNext()) {
   				 T nextItem = itr.next();
   				 int newIndex = itemIndex(nextItem, newtable.length);
   				 if(newtable[newIndex] == null) {
   					 newtable[newIndex] = new LinkedList<T>();
   					 numOccupied++;
   				 }
   				 newtable[newIndex].add(nextItem);
   				 if(newtable[newIndex].size() > currentLongestChainLength) {
   					 currentLongestChainLength = newtable[newIndex].size();	 
   				 }
   				 if(needExpansion()) {
   					 newtable = expand(newtable);
   				 }
   			 }
   		 }
   	 }
   	 return newtable;
    }
    
    private double currentLoadFactor() {
   	 return numOccupied / (double) table.length;
    }
    
    private int nextPrime(int n) {
   	 int nextPrime = n + 1; //How to deal with overflow?
   	 while(!isPrime(nextPrime)) {	 
   			 nextPrime++;
   	 }
   	 return nextPrime; 
    }
    
    private boolean isPrime(int n) {
   	 for (int i = 2; i< n; i++) {
   		 if(n % i == 0) {
   			 return false;
   		 }
   	 }
   	 return true;
    }
    
    private boolean needExpansion() {
   	 return maxChainLength != 0 && currentLongestChainLength > maxChainLength
   			 || currentLoadFactor() > loadfactor;
    }
}
