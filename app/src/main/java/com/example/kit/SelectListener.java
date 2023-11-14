package com.example.kit;

import com.example.kit.data.Item;


/** SelectListener is an interface that is used to implement UI interactions
 * for views that are not visible to the current class. The listener must be
 * passed as an argument into the class where the view is available.
 */
public interface SelectListener {
    void onItemClick(String id);
    void onItemLongClick();
    void onAddTagClick(String id);
}
