!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! [After maintenance the new branch](https://github.com/xuehuiniaoyu/oxpecker) (����Ϊ oxpecker)

# 1.hjson-java

[![Build Status](https://img.shields.io/travis/hjson/hjson-java.svg?style=flat-square)](http://travis-ci.org/hjson/hjson-java)
[![Maven Central](https://img.shields.io/maven-central/v/org.hjson/hjson.svg?style=flat-square)](http://search.maven.org/#search|ga|1|g%3A%22org.hjson%22%20a%3A%22hjson%22)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.hjson/hjson/badge.svg?style=flat-square&color=blue)](http://www.javadoc.io/doc/org.hjson/hjson)

[Hjson](http://hjson.org), the Human JSON. A configuration file format for humans. Relaxed syntax, fewer mistakes, more comments.

![Hjson Intro](http://hjson.org/hjson1.gif)

# 2.Handlebars.java

[![Become a Patreon](https://img.shields.io/badge/patreon-donate-orange.svg)](https://patreon.com/edgarespina)
[![Build Status](https://travis-ci.org/jknack/handlebars.java.svg?branch=master)](https://travis-ci.org/jknack/handlebars.java)
[![coveralls.io](https://img.shields.io/coveralls/jknack/handlebars.java.svg)](https://coveralls.io/r/jknack/handlebars.java?branch=master)
[![codecov](https://codecov.io/gh/jknack/handlebars.java/branch/master/graph/badge.svg)](https://codecov.io/gh/jknack/handlebars.java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jknack/handlebars/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jknack/handlebars)
[![javadoc](https://javadoc.io/badge/com.github.jknack/handlebars.svg)](https://javadoc.io/doc/com.github.jknack/handlebars)

===============

## Logic-less and semantic Mustache templates with Java

```java
Handlebars handlebars = new Handlebars();

Template template = handlebars.compileInline("Hello {{this}}!");

System.out.println(template.apply("Handlebars.java"));
```

Output:

```
Hello Handlebars.java!
```

# 3.Rhino

![Rhino Intro](https://mdn.mozillademos.org/files/663/rhino.jpg)

[Rhino](https://developer.mozilla.org/zh-CN/docs/Mozilla/Projects/Rhino) is an open-source implementation of [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript) written entirely in Java. It is typically embedded into Java applications to provide scripting to end users. It is embedded in J2SE 6 as the default Java scripting engine.

# 4.android_rhinoceros

[github](https://github.com/xuehuiniaoyu/android_rhinoceros)

![](logo.png)

### 4.1 how to

```
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}



dependencies {
    implementation 'com.github.xuehuiniaoyu:oxpecker:v1.1'
}
```

### 4.2 hello world

activity-code

```
public class CustomActivity extends HActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewFromAssets("hello.hj");
    }

}
```

hello.hj

```
{
    body: {
        {{text-view}}: {
            width: auto
            height: auto

            text: "Hello! World!"
            centerInParent: true
        }
    }
}
```

### 4.3 AdapterView is very simple

grid.hj (layout)

```
{{grid-view}}: {
  width: 100
  height: 100
  view: "{{assets}}/hjson/view.hjson"
  data: "{{assets}}/hjson/list_item_data.hjson"
}
```

list_item_data.hjson

```
[
    {
        hello: "���"
        world: "�й�"
    }

    {
        hello: "Hello"
        world: "China"
    }

    {
        hello: "Hello"
        world: "Bird"
    }

    {
        hello: "Hello"
        world: "Rhinoceros"
    }
]
```

view.hjson

```
{
    body: {
        {{text-view}}: {
            width: "50"
            height: "50"
            text: "{{hello}} {{world }}"
        }
    }
}
```

### 4.4 Weight layout

```
{
    body: {
        heightSum: 9

        {{relative-layout}}: {
            id: layout1
            heightWeight: 3
            background: "#00ff00"
        }

        {{relative-layout}}: {
            id: layout2
            below: "#layout1"
            heightWeight: 3
            background: "#a64d79"
        }

        {{relative-layout}}: {
            id: layout3
            below: "#layout2"
            heightWeight: 3
            background: "#45818e"
        }
    }
}
```

### 4.5 LinearLayout

```
{{linear-layout}}: {
  // orien: h
  orien: v
  width: fill
  height: fill

  {{text-view}}: {
  width: fill
  height: 10
  text: hello world
  }

  {{text-view}}: {
  width: fill
  height: 10
  text: hello world
  }

  {{text-view}}: {
  width: fill
  height: 10
  text: hello world
  }
}
```

### 4.6 RelativeLayout

```
{{relative-layout}}: {
    width: 100
    height: 100

    {{text-view}}: {
        width: 40
        height: 30
        asParent: right|bottom
        text: Hello World!
        textAlign:centerH|bottom
    }
}
```

### 4.7 javascript

```
{
    head: {
        script:
            '''
            function onClick(v) {
                __console.toast("javascript click view:"+v);
            }
            '''
    }

    body: {
        {{text-view}}: {
            onClick: "javascript: onClick"
        }
    }
}
```

### 4.8 The custom View (�Զ���View)

1.

```
{
    define: {
        myLayout: "com.xxx.view.MyLayout"
    }

    body: {
        myLayout: {
            customAttr: "value"
        }
    }
}
```

2.

```
public class MyLayout extends GroupWapper<LinearLayout> {
    public MyLayout(Context context, JsonValue value) {
        super(context, value);
    }

    /**
     * �����Զ�������
     * Custom Attr
     * @param value
     */
    public void setCustomAttr(JsonValue value) {
        if(value.isString()) {
            // get "customAttr"
            String customAttr = value.asString();
        }
    }
}
```
