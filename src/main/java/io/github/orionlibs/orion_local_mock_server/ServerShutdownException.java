package io.github.orionlibs.orion_local_mock_server;

public class ServerShutdownException extends Exception
{
    private static final String DefaultErrorMessage = "Failed to close Orion mock server.";


    public ServerShutdownException()
    {
        super(DefaultErrorMessage);
    }


    public ServerShutdownException(String message)
    {
        super(message);
    }


    public ServerShutdownException(String errorMessage, Object... arguments)
    {
        super(String.format(errorMessage, arguments));
    }


    public ServerShutdownException(Throwable cause, String errorMessage, Object... arguments)
    {
        super(String.format(errorMessage, arguments), cause);
    }


    public ServerShutdownException(Throwable cause)
    {
        super(DefaultErrorMessage, cause);
    }
}