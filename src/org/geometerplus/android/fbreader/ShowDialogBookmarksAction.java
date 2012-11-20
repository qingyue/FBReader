/**
 * 
 */
package org.geometerplus.android.fbreader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geometerplus.android.util.UIUtil;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.fbreader.library.Bookmark;
import org.geometerplus.fbreader.library.Library;

import com.onyx.android.sdk.ui.data.BookmarkItem;
import com.onyx.android.sdk.ui.dialog.DialogBookmarks;
import com.onyx.android.sdk.ui.dialog.DialogBookmarks.onDeleteBookmarkListener;
import com.onyx.android.sdk.ui.dialog.DialogBookmarks.onGoToPageListener;

/**
 * @author dxwts
 *
 */
public class ShowDialogBookmarksAction extends FBAndroidAction
{

    ShowDialogBookmarksAction(FBReader baseActivity, FBReaderApp fbreader)
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
        ArrayList<BookmarkItem> bookmarks = new ArrayList<BookmarkItem>();
        List<Bookmark> AllBooksBookmarks;
        
        final FBReaderApp fbreader = (FBReaderApp)FBReaderApp.Instance();
        AllBooksBookmarks = Library.Instance().allBookmarks();
        Collections.sort(AllBooksBookmarks, new Bookmark.ByTimeComparator());

        if (fbreader.Model != null) {
            final long bookId = fbreader.Model.Book.getId();
            for (Bookmark bookmark : AllBooksBookmarks) {
                if (bookmark.getBookId() == bookId) {
                    BookmarkItem item = new BookmarkItem(bookmark.getText(), bookmark);
                    bookmarks.add(item);
                }
            }
        }
        
        final DialogBookmarks dlg = new DialogBookmarks(BaseActivity, bookmarks);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnGoToPageListener(new onGoToPageListener()
        {
            
            @Override
            public void onGoToPage(BookmarkItem item)
            {
                Bookmark bookmark = (Bookmark) item.getTag();
                bookmark.onOpen();
                final FBReaderApp fbreader = (FBReaderApp)FBReaderApp.Instance();
                final long bookId = bookmark.getBookId();
                if ((fbreader.Model == null) || (fbreader.Model.Book.getId() != bookId)) {
                    final Book book = Book.getById(bookId);
                    if (book != null) {
                        dlg.dismiss();
                        fbreader.openBook(book, bookmark, null);
                    } else {
                        UIUtil.showErrorMessage(dlg.getContext(), "cannotOpenBook");
                    }
                } else {
                    dlg.dismiss();
                    fbreader.gotoBookmark(bookmark);
                }
                
            }
        });
        dlg.setOnDeleteBookmarkListener(new onDeleteBookmarkListener()
        {
            
            @Override
            public void DeleteBookmark(BookmarkItem item)
            {
                Bookmark bookmark = (Bookmark) item.getTag();
                bookmark.delete();
            }
        });
        dlg.show();
    }

}
