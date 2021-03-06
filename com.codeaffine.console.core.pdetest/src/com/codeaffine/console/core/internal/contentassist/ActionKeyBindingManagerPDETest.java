package com.codeaffine.console.core.internal.contentassist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.test.util.ConditionalIgnoreRule;
import com.codeaffine.test.util.ConditionalIgnoreRule.ConditionalIgnore;
import com.codeaffine.test.util.GtkPlatform;
import com.codeaffine.test.util.swt.DisplayHelper;
import com.codeaffine.test.util.swt.SWTEventHelper;

public class ActionKeyBindingManagerPDETest {

  @Rule
  public final ConditionalIgnoreRule ignoreRule = new ConditionalIgnoreRule();
  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private ActionKeyBindingManager actionKeyBindingManager;
  private TextViewer textViewer;
  private IAction action;

  @Test
  public void testInitialActiveKeySequence() {
    String activeKeySequence = actionKeyBindingManager.getActiveKeySequence();

    assertThat( activeKeySequence ).isNull();
  }

  @Test
  @ConditionalIgnore(condition=GtkPlatform.class)
  public void testPressMatchingKeySequence() {
    actionKeyBindingManager.addKeyBinding( "Ctrl+Space", action );

    simulateKeyDown( SWT.CTRL, SWT.SPACE );

    verify( action ).run();
    assertThat( actionKeyBindingManager.getActiveKeySequence() ).isEqualTo( "CTRL+SPACE" );
  }

  @Test
  @ConditionalIgnore(condition=GtkPlatform.class)
  public void testPressNonMatchingKeySequence() {
    actionKeyBindingManager.addKeyBinding( "Ctrl+Space", action );

    simulateKeyDown( SWT.NONE, SWT.CR );

    verify( action, never() ).run();
    assertThat( actionKeyBindingManager.getActiveKeySequence() ).isNull();
  }

  @Test
  @ConditionalIgnore(condition=GtkPlatform.class)
  public void testPressNonMatchingAfterMatchingKeySequence() {
    actionKeyBindingManager.addKeyBinding( "Ctrl+Space", action );

    simulateKeyDown( SWT.CTRL, SWT.SPACE );
    simulateKeyDown( SWT.NONE, 'x' );

    verify( action ).run();
    assertThat( actionKeyBindingManager.getActiveKeySequence() ).isEqualTo( "CTRL+SPACE" );
  }

  @Test(expected=IllegalArgumentException.class)
  public void testAddKeyBindingWithInvalidKeySequence() {
    actionKeyBindingManager.addKeyBinding( "nokey", action );
  }

  @Before
  public void setUp() {
    textViewer = new TextViewer( displayHelper.createShell(), SWT.NONE );
    actionKeyBindingManager = new ActionKeyBindingManager();
    actionKeyBindingManager.activateFor( textViewer );
    action = mock( IAction.class );
  }

  private void simulateKeyDown( int modifiers, char keyCode ) {
    SWTEventHelper.trigger( SWT.KeyDown )
      .withStateMask( modifiers )
      .withKeyCode( keyCode )
      .on( textViewer.getControl() );
  }
}
