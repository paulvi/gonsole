package com.codeaffine.console.core.internal.contentassist;

import static com.codeaffine.console.core.internal.contentassist.ConsoleInformationControl.createInformationControlCreator;
import static com.codeaffine.console.core.internal.contentassist.ConsoleInformationControlCreator.Appearance.FIXED;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.console.TextConsoleViewer;

import com.codeaffine.console.core.ConsoleComponentFactory;

public class ContentAssist implements ConsoleContentAssist, DisposeListener {

  private final Editor editor;
  private final ContentAssistProcessor contentAssistProcessor;
  private final ContentAssistant contentAssistant;
  private final TextConsoleViewer textViewer;

  public ContentAssist( TextConsoleViewer textViewer, ConsoleComponentFactory factory  ) {
    this.editor = new Editor( textViewer, this );
    this.contentAssistProcessor = new ContentAssistProcessor( factory, editor );
    this.contentAssistant = new ContentAssistant();
    this.textViewer = textViewer;
  }

  public void install() {
    contentAssistant.enablePrefixCompletion( true );
    contentAssistant.setRepeatedInvocationMode( true );
    contentAssistant.setContentAssistProcessor( contentAssistProcessor, PartitionType.INPUT );
    contentAssistant.setInformationControlCreator( createInformationControlCreator( FIXED ) );
    contentAssistant.install( textViewer );
    textViewer.getTextWidget().addDisposeListener( this );
    textViewer.getTextWidget().addFocusListener( editor );
  }

  @Override
  public void showPossibleCompletions() {
    contentAssistant.showPossibleCompletions();
  }

  @Override
  public void widgetDisposed( DisposeEvent e ) {
    contentAssistant.uninstall();
    contentAssistProcessor.dispose();
  }
}