package org.zoon.rhinoceros.template;

import org.zoon.rhinoceros.layout.widget.HButton;
import org.zoon.rhinoceros.layout.widget.HCanvasView;
import org.zoon.rhinoceros.layout.widget.HEditText;
import org.zoon.rhinoceros.layout.widget.HGridView;
import org.zoon.rhinoceros.layout.widget.HHorizontalScrollView;
import org.zoon.rhinoceros.layout.widget.HImageView;
import org.zoon.rhinoceros.layout.widget.HLinearLayout;
import org.zoon.rhinoceros.layout.widget.HListView;
import org.zoon.rhinoceros.layout.widget.HRelativeLayout;
import org.zoon.rhinoceros.layout.widget.HScrollView;
import org.zoon.rhinoceros.layout.widget.HTextView;

public class SimpleHTemplate extends HTemplate {
    {
        as("linear-layout", HLinearLayout.class.getName());
        as("relative-layout", HRelativeLayout.class.getName());
        as("hscroll-layout", HHorizontalScrollView.class.getName());
        as("vscroll-layout", HScrollView.class.getName());
        as("default-view", HCanvasView.class.getName());
        as("text-view", HTextView.class.getName());
        as("edit-view", HEditText.class.getName());
        as("button-view", HButton.class.getName());
        as("img-view", HImageView.class.getName());
        as("list-view", HListView.class.getName());
        as("grid-view", HGridView.class.getName());
    }
}
