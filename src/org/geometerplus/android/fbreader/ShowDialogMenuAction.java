/**
 * 
 */
package org.geometerplus.android.fbreader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geometerplus.android.util.UIUtil;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.fbreader.library.Bookmark;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.text.model.ZLTextModel;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextView.PagePosition;
import org.geometerplus.zlibrary.text.view.style.ZLTextBaseStyle;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;

import android.content.pm.ActivityInfo;
import android.widget.LinearLayout;

import com.onyx.android.sdk.ui.data.DirectoryItem;
import com.onyx.android.sdk.ui.dialog.DialogDirectory;
import com.onyx.android.sdk.ui.dialog.DialogFontFaceSettings;
import com.onyx.android.sdk.ui.dialog.DialogFontFaceSettings.onSettingsFontFaceListener;
import com.onyx.android.sdk.ui.dialog.DialogGotoPage;
import com.onyx.android.sdk.ui.dialog.DialogGotoPage.AcceptNumberListener;
import com.onyx.android.sdk.ui.dialog.DialogGotoPage.onDismissMenuDialogListener;
import com.onyx.android.sdk.ui.dialog.DialogReaderMenu;
import com.onyx.android.sdk.ui.dialog.DialogReaderMenu.FontSizeProperty;
import com.onyx.android.sdk.ui.dialog.DialogReaderMenu.LineSpacingProperty;
import com.onyx.android.sdk.ui.dialog.DialogReaderMenu.RotationScreenProperty;
/**
 * @author dxwts
 *
 */
public class ShowDialogMenuAction extends FBAndroidAction
{

    private ArrayList<String> mFonts = null;
    private ZLTextBaseStyle mBaseStyle = null;
    
    private DialogReaderMenu mDialogReaderMenu;
    
    ShowDialogMenuAction(FBReader baseActivity, FBReaderApp fbreader)
    {
        super(baseActivity, fbreader);
    }

    /* (non-Javadoc)
     * @see org.geometerplus.zlibrary.core.application.ZLApplication.ZLAction#run(java.lang.Object[])
     */
    @Override
    protected void run(Object... params)
    {
        final ZLTextStyleCollection collection = ZLTextStyleCollection.Instance();
        mBaseStyle = collection.getBaseStyle();
        
        DialogReaderMenu.IMenuHandler menu_handler = new DialogReaderMenu.IMenuHandler()
        {
            
            @Override
            public void updateCurrentPage(LinearLayout l)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void showTTsView()
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void showTOC()
            {
                final FBReaderApp fbreader = (FBReaderApp)FBReaderApp.Instance();
                ArrayList<DirectoryItem> bookmarks = new ArrayList<DirectoryItem>();
                List<Bookmark>allBooksBookmarks = Bookmark.bookmarks();
                Collections.sort(allBooksBookmarks, new Bookmark.ByTimeComparator());

                if (fbreader.Model != null) {
                    final long bookId = fbreader.Model.Book.getId();
                    for (Bookmark bookmark : allBooksBookmarks) {
                        if (bookmark.getBookId() == bookId) {
                            DirectoryItem item = new DirectoryItem(bookmark.getText(),bookmark.getBookmarkPage(),  bookmark);
                            bookmarks.add(item);
                        }
                    }
                }

                final TOCTree tocTree = fbreader.Model.TOCTree;
                ArrayList<DirectoryItem> TOCItems = new ArrayList<DirectoryItem>();
                if(tocTree.hasChildren()) {
                    for (TOCTree t : tocTree) {
                        if(t.getText() != null){
                            ZLTextView zlt = (ZLTextView)ZLApplication.Instance().getCurrentView();
                            ZLTextModel zltModel = zlt.getModel();
                            int textLength = zltModel.getTextLength(t.getReference().ParagraphIndex);
                            DirectoryItem item = new DirectoryItem(t.getText(), zlt.getPageNumber(textLength) + 1, t.getReference().ParagraphIndex);
                            TOCItems.add(item);
                        }
                    }
                }

                DialogDirectory.IGotoPageHandler gotoPageHandler = new DialogDirectory.IGotoPageHandler()
                {

                    @Override
                    public void jumpTOC(DirectoryItem item)
                    {
                        fbreader.addInvisibleBookmark();
                        fbreader.BookTextView.gotoPosition(Integer.parseInt(item.getTag().toString()), 0, 0);
                        fbreader.showBookTextView();
                    }

                    @Override
                    public void jumpBookmark(DirectoryItem item)
                    {
                        Bookmark bookmark = (Bookmark) item.getTag();
                        bookmark.onOpen();
                        final FBReaderApp fbreader = (FBReaderApp)FBReaderApp.Instance();
                        final long bookId = bookmark.getBookId();
                        if ((fbreader.Model == null) || (fbreader.Model.Book.getId() != bookId)) {
                            final Book book = Book.getById(bookId);
                            if (book != null) {
                                fbreader.openBook(book, bookmark, null);
                            } else {
                                UIUtil.showErrorMessage(mDialogReaderMenu.getContext(), "cannotOpenBook");
                            }
                        } else {
                            fbreader.gotoBookmark(bookmark);
                        }
                    }

                    @Override
                    public void jumpAnnotation(DirectoryItem item)
                    {
                        // TODO Auto-generated method stub

                    }
                };

                DialogDirectory dialogDirectory = new DialogDirectory(BaseActivity, TOCItems, bookmarks, null, gotoPageHandler);
                dialogDirectory.show();
            }
            
            @Override
            public void showSetFontView()
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void showLineSpacingView()
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void showBookMarks()
            {
                ZLApplication.Instance().doAction(ActionCode.SHOW_DIALOG_BOOKMARKS);
            }
            
            @Override
            public void setLineSpacing(LineSpacingProperty property)
            {
                if(property == LineSpacingProperty.normal) {
                    ZLIntegerRangeOption option =
                            ZLTextStyleCollection.Instance().getBaseStyle().LineSpaceOption;
                        option.setValue(10);
                        Reader.clearTextCaches();
                        Reader.getViewWidget().repaint();
                    
                } else if (property == LineSpacingProperty.big) {
                    ZLIntegerRangeOption option =
                            ZLTextStyleCollection.Instance().getBaseStyle().LineSpaceOption;
                        option.setValue(15);
                        Reader.clearTextCaches();
                        Reader.getViewWidget().repaint();
                } else if (property == LineSpacingProperty.small) {
                    ZLIntegerRangeOption option =
                            ZLTextStyleCollection.Instance().getBaseStyle().LineSpaceOption;
                        option.setValue(8);
                        Reader.clearTextCaches();
                        Reader.getViewWidget().repaint();
                }
            }
            
            @Override
            public void setFontFace()
            {
                mFonts = new ArrayList<String>();
                final String optionValue = mBaseStyle.FontFamilyOption.getValue();
                AndroidFontUtil.fillFamiliesList(mFonts, true);
                String[] fontfoces = new String[mFonts.size()];
                for (int i = 0; i < mFonts.size(); i++) {
                    fontfoces[i] = mFonts.get(i);
                }
                DialogFontFaceSettings dlg = new DialogFontFaceSettings(BaseActivity, fontfoces, optionValue);
                dlg.show();
                dlg.setOnSettingsFontFaceListener(new onSettingsFontFaceListener()
                {
                    
                    @Override
                    public void settingfontFace(int location)
                    {
                        mBaseStyle.FontFamilyOption.setValue(mFonts.get(location));
                        mDialogReaderMenu.setButtonFontFaceText(mFonts.get(location));
                        Reader.clearTextCaches();
                        Reader.getViewWidget().repaint();
                    }
                });
            }
            
            @Override
            public void searchContent()
            {
                ZLApplication.Instance().doAction(ActionCode.SEARCH);
            }
            
            @Override
            public void rotationScreen(int i)
            {
                if (i == -1) {
                    ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT);
                }
            }
            
            @Override
            public void previousPage()
            {
                ZLApplication.Instance().doAction(ActionCode.TURN_PAGE_BACK);
                updatePage();
            }
            
            @Override
            public void nextPage()
            {
                ZLApplication.Instance().doAction(ActionCode.TURN_PAGE_FORWARD);
                updatePage();
            }
            
            @Override
            public void increaseFontSize()
            {
                ZLApplication.Instance().doAction(ActionCode.INCREASE_FONT);
            }
            
            @Override
            public void gotoPage(int i)
            {
                final ZLTextView view = (ZLTextView) ZLApplication.Instance().getCurrentView();
                if (i == 1) {
                    view.gotoHome();
                } else {
                    view.gotoPage(i);
                }
                ZLApplication.Instance().getCurrentView().Application.getViewWidget().reset();
                ZLApplication.Instance().getCurrentView().Application.getViewWidget().repaint();
            }
            
            @Override
            public int getPageIndex()
            {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public int getPageCount()
            {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public String getFontFace()
            {
                return mBaseStyle.FontFamilyOption.getValue();
            }
            
            @Override
            public void decreaseFontSize()
            {
                ZLApplication.Instance().doAction(ActionCode.DECREASE_FONT);
            }
            
            @Override
            public void changeRotationScreen(RotationScreenProperty property)
            {
                if (property == RotationScreenProperty.rotation_0) {
                    mDialogReaderMenu.dismiss();
                    BaseActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else if (property == RotationScreenProperty.rotation_90) {
                    mDialogReaderMenu.dismiss();
                    BaseActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                else if (property == RotationScreenProperty.rotation_180) {
                    mDialogReaderMenu.dismiss();
                    BaseActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
                else if (property == RotationScreenProperty.rotation_270) {
                    mDialogReaderMenu.dismiss();
                    BaseActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
            
            @Override
            public void changeFontsize(FontSizeProperty property)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void showGoToPageDialog()
            {
                final DialogGotoPage dialogGotoPage = new DialogGotoPage(BaseActivity);
                dialogGotoPage.setAcceptNumberListener(new AcceptNumberListener()
                {

                    @Override
                    public void onAcceptNumber(int num)
                    {
                        final ZLTextView view = (ZLTextView) ZLApplication.Instance().getCurrentView();
                        if (num == 1) {
                            view.gotoHome();
                        } else {
                            view.gotoPage(num);
                        }
                        ZLApplication.Instance().getCurrentView().Application.getViewWidget().reset();
                        ZLApplication.Instance().getCurrentView().Application.getViewWidget().repaint();

                        dialogGotoPage.dismiss();
                        mDialogReaderMenu.dismiss();
                    }
                });
                dialogGotoPage.setOnDismissMenuDialogListener(new onDismissMenuDialogListener()
                {

                    @Override
                    public void dismissMenudialog()
                    {
                        mDialogReaderMenu.dismiss();
                    }
                });
                dialogGotoPage.show();
            }

            @Override
            public void zoomToPage()
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void zoomToWidth()
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void zoomToHeight()
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void zoomBySelection()
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void zoomByTwoPoints()
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void zoomByValue(double z)
            {
                // TODO Auto-generated method stub
                
            }
        };
        
        mDialogReaderMenu = new DialogReaderMenu(BaseActivity, menu_handler);
        mDialogReaderMenu.setCanceledOnTouchOutside(true);
        updatePage();
        mDialogReaderMenu.show();

    }
    
    private void updatePage() {
        ZLApplication ZLApp = ZLApplication.Instance();
        FBView view = (FBView) ZLApp.getCurrentView();
        final PagePosition pagePosition = view.pagePosition();
        mDialogReaderMenu.setPageIndex(pagePosition.Current);
        mDialogReaderMenu.setPageCount(pagePosition.Total);
    }

}
