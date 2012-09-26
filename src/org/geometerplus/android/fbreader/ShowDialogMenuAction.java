/**
 * 
 */
package org.geometerplus.android.fbreader;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;

import com.onyx.android.sdk.ui.dialog.DialogMenu;
import com.onyx.android.sdk.ui.dialog.DialogMenu.onChangePageLinsener;
import com.onyx.android.sdk.ui.dialog.DialogMenu.onDecreaseFontLinsener;
import com.onyx.android.sdk.ui.dialog.DialogMenu.onIncreaseFontLinsener;
import com.onyx.android.sdk.ui.dialog.DialogMenu.onOpenTOCLinsener;
import com.onyx.android.sdk.ui.dialog.DialogMenu.onRotationScreenLinsener;
import com.onyx.android.sdk.ui.dialog.DialogMenu.*;
/**
 * @author dxwts
 *
 */
public class ShowDialogMenuAction extends FBAndroidAction
{

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
        DialogMenu dlg = new DialogMenu(BaseActivity);
        dlg.setCanceledOnTouchOutside(true);
       dlg.setOnIncreaseFontLinsener(new onIncreaseFontLinsener()
    {
        
        @Override
        public void IncreaseFont()
        {
            ZLApplication.Instance().doAction(ActionCode.INCREASE_FONT);
            
        }
    });
       dlg.setOnDecreaseFontLinener(new onDecreaseFontLinsener()
    {
        
        @Override
        public void DecreaseFont()
        {
            ZLApplication.Instance().doAction(ActionCode.DECREASE_FONT);
            
        }
    });
       dlg.setOnChangePageLinsener(new onChangePageLinsener()
    {
        
        @Override
        public void ChangePage(int i)
        {
            if(i == -1) {
                ZLApplication.Instance().doAction(ActionCode.TURN_PAGE_BACK);
            } else {
                ZLApplication.Instance().doAction(ActionCode.TURN_PAGE_FORWARD);
            }
            
        }
    });
   dlg.setOnRotationScreenLinsener(new onRotationScreenLinsener()
{
    
    @Override
    public void RotationScreen(int i)
    {
        if(i == -1) {
            ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            ZLApplication.Instance().doAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT);
        }
        
    }
});
   dlg.setOnOpenTOCLinsener(new onOpenTOCLinsener()
{
    
    @Override
    public void OpenTOCLinsener()
    {
        ZLApplication.Instance().doAction(ActionCode.SHOW_DIALOG_TOC);
        
    }
});
   dlg.setOnSearchContentLinsener(new onSearchContentLinsener()
{
    
    @Override
    public void SearchContent()
    {
        ZLApplication.Instance().doAction(ActionCode.SEARCH);
        
    }
});
   dlg.setOnShowBookMarkLinsener(new onShowBookMarkLinsener()
{
    
    @Override
    public void ShowBookMark()
    {
        ZLApplication.Instance().doAction(ActionCode.SHOW_BOOKMARKS);
        
    }
});
        dlg.show();

    }

}
