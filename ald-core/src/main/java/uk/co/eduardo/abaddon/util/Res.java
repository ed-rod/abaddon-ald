package uk.co.eduardo.abaddon.util;

import android.content.res.Resources;

/**
 * Typically only descendants of <code>View</code> have access to the resources. This holds onto a reference to the resources to
 * make them global and accessible from anywhere
 *
 * @author Ed
 */
public class Res
{
   // ================| Fields |====================================

   /** Global resources */
   public static Resources resources;
}
