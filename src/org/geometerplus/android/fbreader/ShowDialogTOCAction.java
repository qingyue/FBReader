/**
 * 
 */
package org.geometerplus.android.fbreader;

import java.util.ArrayList;

import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.text.model.ZLTextModel;
import org.geometerplus.zlibrary.text.view.ZLTextView;

import com.onyx.android.sdk.ui.data.TOCItem;
import com.onyx.android.sdk.ui.dialog.DialogTOC;
import com.onyx.android.sdk.ui.dialog.DialogTOC.onGoToPageListener;

/**
 * @author dxwts
 * 
 */
public class ShowDialogTOCAction extends FBAndroidAction
{

    ShowDialogTOCAction(FBReader baseActivity, FBReaderApp fbreader)
    {
        super(baseActivity, fbreader);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.geometerplus.zlibrary.core.application.ZLApplication.ZLAction#run
     * (java.lang.Object[])
     */
    @Override
    protected void run(Object... params)
    {
        final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
        final TOCTree tocTree = fbreader.Model.TOCTree;
        ArrayList<TOCItem> TOCItems = new ArrayList<TOCItem>();
        if(tocTree.hasChildren()) {
            for (TOCTree t : tocTree) {
                if(t.getText() != null){
                    ZLTextView zlt = (ZLTextView)ZLApplication.Instance().getCurrentView();
                    ZLTextModel zltModel = zlt.getModel();
                    int textLength = zltModel.getTextLength(t.getReference().ParagraphIndex);
                    TOCItem item = new TOCItem(t.getText(), zlt.getPageNumber(textLength) + 1, t.getReference().ParagraphIndex);
                    TOCItems.add(item);
                }
            }
        }
        DialogTOC dlg = new DialogTOC(BaseActivity, TOCItems);
        dlg.setOnGoToPageListener(new onGoToPageListener()
        {

            @Override
            public void onGoToPage(TOCItem item)
            {
                final FBReaderApp fbreader = (FBReaderApp)ZLApplication.Instance();
                fbreader.addInvisibleBookmark();
                fbreader.BookTextView.gotoPosition(Integer.parseInt(item.getTag().toString()), 0, 0);
                fbreader.showBookTextView();
                
            }

        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();

    }

}
