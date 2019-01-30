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

### how to 
```
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}



dependencies {
	implementation 'com.github.xuehuiniaoyu:android_rhinoceros:Tag'
}
```

### hello world

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