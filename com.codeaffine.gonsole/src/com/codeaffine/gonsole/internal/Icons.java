package com.codeaffine.gonsole.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;


public class Icons {

  public static final String GONSOLE = "icons/etool16/gonsole.png";

  public static ImageDescriptor getDescriptor( String name ) {
    ImageDescriptor result = null;
    ImageRegistry imageRegistry = getImageRegistry();
    if( imageRegistry != null ) {
      result = imageRegistry.getDescriptor( name );
    }
    return result;
  }


  static void registerImages( ImageRegistry registry ) {
    Field[] declaredFields = Icons.class.getDeclaredFields();
    for( Field field : declaredFields ) {
      if( isStringConstant( field ) ) {
        registerImage( registry, getStringValue( field ) );
      }
    }
  }

  private static void registerImage( ImageRegistry registry, String imageName ) {
    registry.put( imageName, GonsolePlugin.getInstance().newImageDescriptor( imageName ) );
  }

  private static ImageRegistry getImageRegistry() {
    ImageRegistry result = null;
    GonsolePlugin pluginInstance = GonsolePlugin.getInstance();
    if( pluginInstance != null && pluginInstance.getBundle().getState() == Bundle.ACTIVE ) {
      result = pluginInstance.getImageRegistry();
    }
    return result;
  }

  private static String getStringValue( Field field ) {
    try {
      return ( String )field.get( null );
    } catch( IllegalAccessException iae ) {
      throw new RuntimeException( iae );
    }
  }

  private static boolean isStringConstant( Field field ) {
    int modifiers = field.getModifiers();
    Class<?> type = field.getType();
    return Modifier.isFinal( modifiers ) && Modifier.isStatic( modifiers ) && type == String.class;
  }

  private Icons() {
    // prevent instance creation
  }
}
