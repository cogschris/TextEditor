package server;

import java.util.Formatter;
import java.util.HashMap;

public class Commands {
	
	public enum Command {
		Login, Signup, GetUserFileNames, OpenUserFile, SaveUserFile, Success, Failure, Heartbeat, Userlist, RemoveUser, AddUser,
		GetSharedFiles,GetSharedFile, GetSharedUsers, IsOwner, GetSharedtome, OpenShareFile
	}
	
	private static final HashMap<Command, String> mLogFormatMap;
	private static final String LoginLogFormat = "%s attempt User:%s Pass:%s";
	private static final String SignupLogFormat = "%s attempt User:%s Pass:%s";
	
	static {
		mLogFormatMap = new HashMap<Command, String>();
		mLogFormatMap.put(Command.Login, LoginLogFormat);
		mLogFormatMap.put(Command.Signup, SignupLogFormat);
	}
	
	public static String buildCommand(Command command, String... args) {
		StringBuilder sb = new StringBuilder();
		sb.append(command);
		for(String arg : args) sb.append(" "+arg);
		return sb.toString();
	}

	public static String logCommand(Object[] split) {
		String format = mLogFormatMap.get(Command.valueOf(split[0].toString()));
		if(format == null) return null;
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format(format, split);
		formatter.close();
		return sb.toString();
	}
}
