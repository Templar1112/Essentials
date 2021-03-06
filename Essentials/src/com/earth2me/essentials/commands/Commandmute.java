package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandmute extends EssentialsCommand
{
	public Commandmute()
	{
		super("mute");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		boolean nomatch = false;
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		User user;
		try
		{
			user = getPlayer(server, args, 0, true, true);
		}
		catch (NoSuchFieldException e)
		{
			nomatch = true;
			user = ess.getUser(new OfflinePlayer(args[0], ess));
		}
		if (!user.isOnline())
		{
			if (sender instanceof Player
				&& !ess.getUser(sender).isAuthorized("essentials.mute.offline"))
			{
				throw new Exception(_("muteExempt"));
			}
		}
		else
		{
			if (user.isAuthorized("essentials.mute.exempt") && sender instanceof Player)
			{
				throw new Exception(_("muteExempt"));
			}
		}
		
		long muteTimestamp = 0;

		if (args.length > 1)
		{
			final String time = getFinalArg(args, 1);
			muteTimestamp = DateUtil.parseDateDiff(time, true);
			user.setMuted(true);
		}
		else
		{
			user.setMuted(!user.getMuted());
		}
		user.setMuteTimeout(muteTimestamp);
		final boolean muted = user.getMuted();
		String muteTime = DateUtil.formatDateDiff(muteTimestamp);
		
		if (nomatch)
		{
			sender.sendMessage(_("userUnknown", user.getName()));
		}
		
		if (muted)
		{
			if (muteTimestamp > 0)
			{
				sender.sendMessage(_("mutedPlayerFor", user.getDisplayName(), muteTime));
				user.sendMessage(_("playerMutedFor", muteTime));
			}
			else
			{
				sender.sendMessage(_("mutedPlayer", user.getDisplayName()));
				user.sendMessage(_("playerMuted"));
			}
			ess.broadcastMessage("essentials.mute.notify", _("muteNotify", sender.getName(), user.getName(), muteTime));
		}
		else
		{
			sender.sendMessage(_("unmutedPlayer", user.getDisplayName()));
			user.sendMessage(_("playerUnmuted"));
		}
	}
}
