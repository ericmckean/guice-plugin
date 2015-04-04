## **Alpha** Release ##

This is an alpha release of the plugin.  Please update often.

## Installation Overview ##

The Eclipse plugin is installed like any other Eclipse plugin: from an install site.
The install site is
> http://guice-plugin.googlecode.com/svn/trunk/eclipse-update-site/


### Step by Step Guide ###

  1. In Eclipse, choose from the **Help Menu** the **Software Updates / Find and Install** item.
  1. Choose **Search for new features to install** and click **Next**
  1. Click **New Remote Site...**
  1. Enter **Guice Plugin** for the Name
  1. Enter **http://guice-plugin.googlecode.com/svn/trunk/eclipse-update-site/**for the URL
  1. Click **OK** and then **Finish**
  1. Check the **Guice Plugin** box and hit **Finish**
  1. Click the **I accept the terms in the license agreement** button (after being sure that you do) and click **Next**
  1. Click **Finish**
  1. After the download is complete, click **Install**
  1. When prompted, restart Eclipse by saying **Yes**

### Implementing Iterable

&lt;Module&gt;

 ###

To make the best use of the plugin, create classes that implement Iterable

&lt;Module&gt;

 and return the set of modules you would pass to Guice.createInjector.  The plugin will automatically detect such classes and by default will use them as a context for finding bindings.