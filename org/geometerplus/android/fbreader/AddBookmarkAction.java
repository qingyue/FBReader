/**
 * 
 */
package org.geometerplus.android.fbreader;

import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.library.Bookmark;

/**
 * @author dxwts
 *
 */
public class AddBookmarkAction extends FBAndroidAction
{

    AddBookmarkAction(FBReader baseActivity, FBReaderApp fbreader)
    {
        super(baseActivity, fbreader);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.geometerplus.zlibrary.core.application.ZLApplication.ZLAction#run(java.lang.Object[])
     */
    @Override
    protected void run(Object... params)
    {
        final FBReaderApp fbreader = (FBReaderApp)FBReaderApp.Instance();
        final Bookmark bookmark = fbreader.addBookmark(20, true);
        bookmark.save();
    }

}
