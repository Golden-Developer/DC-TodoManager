package de.goldendeveloper.todomanager.discord.commands;

import de.goldendeveloper.dcbcore.DCBot;
import de.goldendeveloper.dcbcore.interfaces.CommandInterface;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.todomanager.Main;
import de.goldendeveloper.todomanager.MysqlConnection;
import de.goldendeveloper.todomanager.discord.utility.TodoList;
import de.goldendeveloper.todomanager.discord.utility.TodoTypes;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.stream.Collectors;

public class Todo implements CommandInterface {

    public static final String cmdTodo = "todo-list";
    public static final String cmdTodoSubAdd = "add";
    public static final String cmdTodoSubRemove = "remove";
    public static final String cmdTodoSubSetStatus = "set-status";
    public static final String cmdTodoOptionTodo = "todo-id";
    public static final String cmdTodoSubSetStatusOptionStatus = "status";

    @Override
    public CommandData commandData() {
        return Commands.slash(cmdTodo, "Todo-Listen verwalten")
                .setGuildOnly(true)
                .addSubcommands(
                        new SubcommandData(cmdTodoSubAdd, "Fügt eine neue Aufgabe der Todo-List hinzu!"),
                        new SubcommandData(cmdTodoSubRemove, "Entfernt eine vorhandene Aufgabe von der Todo-List!").addOption(OptionType.STRING, cmdTodoOptionTodo, "Hier die Todo-ID eintragen!", true),
                        new SubcommandData(cmdTodoSubSetStatus, "Setzt den Status einer vorhandenen Aufgabe auf der Todo-List!")
                                .addOption(OptionType.STRING, cmdTodoSubSetStatusOptionStatus, "Hier den neun Status des Todos eintagen!", true, true)
                                .addOption(OptionType.STRING, cmdTodoOptionTodo, "Hier die Todo-ID eintragen!", true)
                );
    }

    @Override
    public void runSlashCommand(SlashCommandInteractionEvent e, DCBot dcBot) {
        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
        String roleID = table.getRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId()).getData().get(MysqlConnection.clmPermRole).getAsString();
        Role role = e.getGuild().getRoleById(roleID);
        if (role != null) {
            if (hasRole(role, e.getMember()) && e.getSubcommandName() != null) {
                switch (e.getSubcommandName()) {
                    case cmdTodoSubAdd -> TodoList.add(e);
                    case cmdTodoSubRemove -> TodoList.remove(e);
                    case cmdTodoSubSetStatus -> TodoList.setStatus(e);
                }
            } else {
                e.reply("Dazu hast du keine Rechte! Dir fehlt die Rolle: " + role.getName()).queue();
            }
        } else {
            e.reply("Die Rolle mit der ID: " + roleID + " existiert nicht mehr! Bitte den Bot neu einladen!").queue();
        }
    }

    @Override
    public void runCommandAutoComplete(CommandAutoCompleteInteractionEvent e, DCBot dcBot) {
        if (e.getSubcommandName().equalsIgnoreCase(Todo.cmdTodoSubSetStatus) && e.getFocusedOption().getName().equalsIgnoreCase(Todo.cmdTodoSubSetStatusOptionStatus)) {
            e.replyChoices(
                    TodoTypes.getAllTodoTypes().stream().map(type -> new Command.Choice(type.getName(), type.getValue())).collect(Collectors.toList())
            ).queue();
        }
    }
}
