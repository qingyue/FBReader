/**
 * 
 */
package org.geometerplus.android.fbreader;

import java.util.ArrayList;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextView.PagePosition;
import org.geometerplus.zlibrary.text.view.style.ZLTextBaseStyle;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;

import android.widget.LinearLayout;

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
                ZLApplication.Instance().doAction(ActionCode.SHOW_DIALOG_TOC);
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
                // TODO Auto-generated method stub
                
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
