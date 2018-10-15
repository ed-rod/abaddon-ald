package uk.co.eduardo.abaddon.core.preference;

/**
 * Preference
 * 
 * @author Ed
 * @param <T> the type of the preference.
 */
public interface Preference< T >
{
   /**
    * @return the value of the preference.
    */
   public T get();
   
   /**
    * @return the unique preference name.
    */
   public String getName();
}
