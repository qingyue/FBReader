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

import com.onyx.android.sdk.ui.dialog.DialogFontFaceSettings;
import com.onyx.android.sdk.ui.dialog.DialogFontFaceSettings.onSettingsFontFaceListener;
import com.onyx.android.sdk.ui.dialog.DialogMenu;
import com.onyx.android.sdk.ui.dialog.DialogMenu.*;
/**
 * @author dxwts
 *
 */
public class ShowDialogMenuAction extends FBAndroidAction
{

    private ArrayList<String> mFonts = null;
    private ZLTextBaseStyle mBaseStyle = null;
    
    private DialogMenu mDialogMenu;
    ShowDialogMenuAction(FBReader baseActivity, FBReaderApp fbreader)
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
        DialogMenu dlgMenu = new DialogMenu(BaseActivity);
        dlgMenu.setCanceledOnTouchOutside(true);
        mDialogMenu = dlgMenu.Instance();
        dlgMenu.setOnIncreaseFontLinsener(new onIncreaseFontLinsener()
        {

            @Override
            public void IncreaseFont()
            {
                ZLApplication.Instance().doAction(ActionCode.INCREASE_FONT);

            }
        });
        dlgMenu.setOnDecreaseFontLinener(new onDecreaseFontLinsener()
        {

            @Override
            public void DecreaseFont()
            {
                ZLApplication.Instance().doAction(ActionCode.DECREASE_FONT);

            }
        });
        dlgMenu.setOnChangePageLinsener(new onChangePageLinsener()
        {

            @Override
            public void ChangePage(int i)
            {
                if (i == -1) {
                    ZLApplication.Instance().doAction(ActionCode.TURN_PAGE_BACK);
                    updatePage();
                }
                else if(i == 1) {
                    ZLApplication.Instance().doAction(ActionCode.TURN_PAGE_FORWARD);
                    updatePage();
                } else {
                    final ZLTextView view = (ZLTextView) ZLApplication.Instance().getCurrentView();
                    if (i == 1) {
                        view.gotoHome();
                    } else {
                        view.gotoPage(i);
                    }
                    ZLApplication.Instance().getCurrentView().Application.getViewWidget().reset();
                    ZLApplication.Instance().getCurrentView().Application.getViewWidget().repaint();
                }

            }
        });
        dlgMenu.setOnRotationScreenLinsener(new onRotationScreenLinsener()
        {

            @Override
            public void RotationScreen(int i)
            {
                if (i == -1) {
                    ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT);
                }

            }
        });
        dlgMenu.setOnOpenTOCLinsener(new onOpenTOCLinsener()
        {

            @Override
            public void OpenTOCLinsener()
            {
                ZLApplication.Instance().doAction(ActionCode.SHOW_DIALOG_TOC);

            }
        });
        dlgMenu.setOnSearchContentLinsener(new onSearchContentLinsener()
        {

            @Override
            public void SearchContent()
            {
                ZLApplication.Instance().doAction(ActionCode.SEARCH);

            }
        });
        dlgMenu.setOnShowBookMarkLinsener(new onShowBookMarkLinsener()
        {

            @Override
            public void ShowBookMark()
            {
                ZLApplication.Instance().doAction(ActionCode.SHOW_DIALOG_BOOKMARKS);

            }
        });
        
        dlgMenu.setOnChangeRotationScreenLinsener(new onChangeRotationScreenLinsener()
        {
            
            @Override
            public void changeRotationScreen(RotationScreenProperty property)
            {
                if (property == RotationScreenProperty.rotation_0) {
                    ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT);
                }
                else if (property == RotationScreenProperty.rotation_90) {
                    ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE);
                }
                else if (property == RotationScreenProperty.rotation_180) {
                    ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
                else if (property == RotationScreenProperty.rotation_270) {
                    ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }

            }
        });
        dlgMenu.setOnSettingsLineSpacingLinener(new onSettingsLineSpacingLinsener()
        {
            
            @Override
            public void SettingsLineSpacing(LineSpacingProperty property)
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
        });
        final ZLTextStyleCollection collection = ZLTextStyleCollection.Instance();
        mBaseStyle = collection.getBaseStyle();
        dlgMenu.setOnSettingsFontFaceLinsener(new onSettingsFontFaceLinsener()
        {
            
            @Override
            public void settingsFontFace()
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
                        mDialogMenu.setButtonFontFaceText(mFonts.get(location));
                        Reader.clearTextCaches();
                        Reader.getViewWidget().repaint();
                    }
                });
            }
        });
        if(mBaseStyle.FontFamilyOption.getValue() != null) {
            dlgMenu.setButtonFontFaceText(mBaseStyle.FontFamilyOption.getValue());
        }
        updatePage();
        dlgMenu.show();

    }
    
    private void updatePage() {
        ZLApplication ZLApp = ZLApplication.Instance();
        FBView view = (FBView) ZLApp.getCurrentView();
        final PagePosition pagePosition = view.pagePosition();
        mDialogMenu.setCurrentPage(Integer.toString(pagePosition.Current));
        mDialogMenu.setTotalPage(Integer.toString(pagePosition.Total));
    }

}
