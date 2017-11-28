/**
 * 
 */
package kr.co.kpcard.bots.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * @author chris
 *
 */
public class HelpCommand extends BotCommand {

	private static final Logger logger = LoggerFactory.getLogger(HelpCommand.class);
	
    private final ICommandRegistry commandRegistry;

	/**
	 * @param commandIdentifier
	 * @param description
	 * @param commandRegistry
	 */
	public HelpCommand(String commandIdentifier, String description, ICommandRegistry commandRegistry) {
		super(commandIdentifier, description);
		this.commandRegistry = commandRegistry;
	}

	/* (non-Javadoc)
	 * @see org.telegram.telegrambots.bots.commandbot.commands.BotCommand#execute(org.telegram.telegrambots.bots.AbsSender, org.telegram.telegrambots.api.objects.User, org.telegram.telegrambots.api.objects.Chat, java.lang.String[])
	 */
	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		// TODO Auto-generated method stub
        StringBuilder helpMessageBuilder = new StringBuilder("<b>Help</b>\n");
        helpMessageBuilder.append("These are the registered commands for this Bot:\n\n");

        for (BotCommand botCommand : commandRegistry.getRegisteredCommands()) {
            helpMessageBuilder.append(botCommand.toString()).append("\n\n");
        }
        logger.info(helpMessageBuilder.toString());

        SendMessage helpMessage = new SendMessage();
        helpMessage.setChatId(chat.getId().toString());
        helpMessage.enableHtml(true);
        helpMessage.setText(helpMessageBuilder.toString());

        try {
            absSender.execute(helpMessage);
        } catch (TelegramApiException e) {
        	logger.error(e.getMessage());
        }
	}

}
