package lt.artsysoft.compiler.iform_generator;

import lt.artsysoft.compiler.beans.Function;
import lt.artsysoft.compiler.beans.TACodeItem;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.6
 * Time: 20.35
 * To change this template use File | Settings | File Templates.
 */
public class TACodeItemsList {

    private static LinkedList<TACodeItem> taCodeItems;

    public static LinkedList<TACodeItem> getList() {
        if (taCodeItems == null) {
            taCodeItems = new LinkedList<>();
        }
        return taCodeItems;
    }

    public static int getMainCodeItemIndex() {
        for (TACodeItem taCodeItem : taCodeItems) {
             if (taCodeItem.getOperation() != null && taCodeItem.getOperation().equals(TACOperations.MAIN)) {
                 return taCodeItems.indexOf(taCodeItem);
             }
        }
        return -1;
    }

}
