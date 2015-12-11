/* ParseSpinnerQueryAdapter.java
 * Purpose: Simple subclass of ParseQueryAdapter to make it work when targeting SDK > L19
 *
 * Created by Drew on 11/29/2015.
 */

package ca.secondlifestory.adapters;

import android.content.Context;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Subclass of ParseQueryAdapter for use with Spinners
 */
public class ParseSpinnerQueryAdapter<T extends ParseObject> extends ParseQueryAdapter<T> {

    public ParseSpinnerQueryAdapter(Context context, Class<? extends ParseObject> clazz) {
        super(context, clazz);
    }

    public ParseSpinnerQueryAdapter(Context context, String className) {
        super(context, className);
    }

    public ParseSpinnerQueryAdapter(Context context, Class<? extends ParseObject> clazz, int itemViewResource) {
        super(context, clazz, itemViewResource);
    }

    public ParseSpinnerQueryAdapter(Context context, String className, int itemViewResource) {
        super(context, className, itemViewResource);
    }

    public ParseSpinnerQueryAdapter(Context context, QueryFactory<T> queryFactory) {
        super(context, queryFactory);
    }

    public ParseSpinnerQueryAdapter(Context context, QueryFactory<T> queryFactory, int itemViewResource) {
        super(context, queryFactory, itemViewResource);
    }

    @Override
    public int getViewTypeCount() {
        // Simple fix to make ParseQueryAdapter work for spinners
        return 1;
    }
}
