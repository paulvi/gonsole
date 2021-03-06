package com.codeaffine.gonsole.internal.interpreter;

import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.getFirst;

import java.util.Arrays;

import com.codeaffine.console.core.ConsoleCommandInterpreter;
import com.codeaffine.console.core.ConsoleOutput;
import com.codeaffine.gonsole.internal.repository.CompositeRepositoryProvider;

public class GitCommandInterpreter implements ConsoleCommandInterpreter {

  private static final String[] WORK_DIR_MODIFY_COMMANDS
    = new String[] { "checkout", "reset", "merge" };

  private final CommandExecutor commandExecutor;
  private final CommandLineParser commandLineParser;

  public GitCommandInterpreter( ConsoleOutput consoleOutput,
                                CompositeRepositoryProvider repositoryProvider )
  {
    this( new CommandExecutor( consoleOutput, repositoryProvider.getCurrentRepositoryLocation() ),
          new CommandLineParser() );
  }

  GitCommandInterpreter( CommandExecutor commandExecutor, CommandLineParser commandLineParser ) {
    this.commandExecutor = commandExecutor;
    this.commandLineParser = commandLineParser;
  }

  @Override
  public boolean isRecognized( String... commandLine ) {
    return commandLineParser.isRecognized( commandLine );
  }

  @Override
  public String execute( String... commandLine ) {
    CommandInfo commandInfo = commandLineParser.parse( commandLine );
    String result = commandExecutor.execute( commandInfo );
    refreshAffectedResources( commandLine );
    return result;
  }

  private void refreshAffectedResources( String... commandLine ) {
    String command = getFirst( Arrays.asList( commandLine ), "" );
    if( contains( Arrays.asList( WORK_DIR_MODIFY_COMMANDS ), command ) ) {
      new SharedResourcesRefresher( commandExecutor.getRepositoryLocation() ).refresh();
    }
  }
}