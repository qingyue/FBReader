/**
 * 
 */
package org.geometerplus.fbreader.fbreader;

import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;

/**
 * @author dxwts
 *
 */
public class ChangeLineSpacingAction extends FBAction
{

    private final int myDelta;
    public ChangeLineSpacingAction(FBReaderApp fbreader, int d)
    {
        super(fbreader);
        myDelta = d;
    }

    /* (non-Javadoc)
     * @see org.geometerplus.zlibrary.core.application.ZLApplication.ZLAction#run(java.lang.Object[])
     */
    @Override
    protected void run(Object... params)
    {
        ZLIntegerRangeOption option =
                ZLTextStyleCollection.Instance().getBaseStyle().LineSpaceOption;
            option.setValue(option.getValue() + myDelta);
            Reader.clearTextCaches();
            Reader.getViewWidget().repaint();

    }

}
