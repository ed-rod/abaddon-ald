package uk.co.eduardo.abaddon.ald.data;

import uk.co.eduardo.map.sections.FileSection;

/**
 * Implementors of this interface generate a {@link FileSection} object.
 *
 * @author Ed
 */
public interface FileSectionAdaptor
{
   /**
    * @return a new FileSection for map
    */
   FileSection createFileSection();
}
