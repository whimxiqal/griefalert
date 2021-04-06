# GriefAlert  
  
A grief alerting plugin for [MinecraftOnline](https://www.minecraftonline.com), 
built on [Sponge](https://www.spongepowered.org/) in combination with
[Prism](https://www.github.com/prism/Prism), 
[WorldEdit](https://ore.spongepowered.org/EngineHub/WorldEdit), and 
[Holograms](https://github.com/randombyte-developer/holograms).
  
## Commands  
All commands have a subcommand **help | ?** to display usage and further subcommands.
- **/griefalert | ga** ...  
    - Central command for GA. By default, displays welcome splash screen  
    - **reload**
        - Reload the configuration file and the Grief Profile database into server memory
    - **check | c** <*index*>  
        - Investigate an Alert at index  
        - Bestows temporary invulnerability  
    - **return | r**
        - Return to the previous known location prior to an Alert check
    - **info | i** <*index*>  
        - Send detailed information about the Alert at *index* through chat  
    - **show | s** <*index*>
        - Display an informative hologram at the location of the Alert at *index*
    - **fix | f** <*index*>
        - Attempt to rollback a specific index to its previous state
    - **query | q** [flags...]
        - Filtering with specific flags, query the Alert cache
        - Flags: Partial Username, GA Event, Partial Target, Maximum, Spread, Group
    - **profile | p** 
        - **add | a** <*event*> <*target*> [flags...]
            - Adds a Grief Profile to the database.
            - *event*: GA Event
            - *target*: GA Target
            - flags: Ignore, Event Color, Target Color, Dimension Color
        - **remove | r** <*event*> <*target*>
            - Removes a Grief Profile to the database.
            - *event*: GA Event
            - *target*: GA Target
        - **count | c**
            - Returns the total number of Grief Profiles currently in use.
        - **list | l**
            - List all Grief Profiles currently in use
        - **events**
            - List all GA Events
    - **logs | l** ...  
        - **inspect | i**  
            - Enable or disable Prism's inspector tool  
            - Same command as Prism's "/pr i" 
        - **[flags...]**
            - Filtering with specific flags and a selected WorldEdit region, query the all Prism logs 
            - Flags: Time Since, Time Before, Exact Username, Prism Target, Prism Event, and Group
    - **rollback | rb** ...  
        - Filtering with specific flags and a selected WorldEdit region, rollback all events stored in Prism to original state
        - Flags: Time Since, Time Before, Exact Username, Prism Target, and Prism Event  
    - **restore | rs** ...  
        - Filtering with specific flags and a selected WorldEdit region, restore all events stored in Prism to more recent state
        - Flags: Time Since, Time Before, Exact Username, Prism Target, and Prism Event  
    - **flush**
        - Removes all cached Alerts

### Argument Syntax  
| Symbol | Meaning  |   
| :----: | :------- |
| <...>  | required |  
| [...]  | optional |

### Flags/Parameters

These are the flags and parameters available for commands. 
Only if the specified flag requirements are satisfied will a query returned to the executor.

| Name             | Label | Explanation | 
| :---:            | :---- | :---------- |
| Alert Index      | *index*     | Integer corresponding to location of Alert in cache |
| Maximum          | *max*       | Self-explanatory |
| Time Since       | *since*     | (Date Format) The earliest date in a range |
| Time Before      | *before*    | (Date Format) The latest date in a range |
| Exact Username   | *player*    | Exact (case-sensitive) username |
| Partial Username | *player*    | A substring of a username |
| Prism Target     | *target*    | The exact Minecraft ID of an object. This includes the domain, i.e. "minecraft:", except entities. |
| GA Target        | *target*    | The Minecraft ID of an object. Domain specification "minecraft:" is implied and not necessary. |
| Partial Target   | *target*    | A substring of any Minecraft ID |
| Prism Event      | *event*     | The name indicative of the type of event. Event types: {break, decay, grow, place, death, command, close, open, drop, insert, pickup, remove, disconnect, join}
| GA Event         | *event*     | The name indicative of the type of event. Event types: {break, place, death, use, apply, interact, attack, replace}
| Dimension        | *dimension* | {minecraft:overworld, minecraft:nether, minecraft:the_end} |
| Ignore           | *ignore*    | A world name such that the world is ignored |
| (...) Color      | *..._color* | Specifies the color for a specific portion (...) of text. Color types: {black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, gray, dark_gray, blue, green, aqua, read, light_purple, yellow, white} |
| Spread           | *spread*    | Return all results of a query separately |
| Group            | *group*     | Return all results of a query collapsed together where possible |

### Date Format

The output format for dates/times can be defined in the configuration file.

The input syntax for dates/times can be done in one of two ways: 
Either use the format "yyyy-MM-dd" to establish the exact day or give a time period to subtract from the current time. 
The time period is formatted using a series of integer values, each combined with a symbol corresponding to the time unit.

| Symbol | Time Unit |
| :----: | :-------- |
| s      | Second    |
| m      | Minute    |
| h      | Hour      |
| d      | Day       |
| w      | Week      |
| M      | Month     |
| y      | year      |

#### Examples
| Format      | Meaning |
| -----:      | :------ |
| 1d12h       | 1 day and 12 hours ago |
| 2w5d        | 2 weeks and 5 days ago |
| 10m1h1d | 1 day, 1 hour and 10 minutes ago |

### Old Commands
MinecraftOnline used to have different commands associated with the older version of GriefAlert. The following commands have been changed:

| <1.7.10  | >1.12.2 |
| ---:     | :------ |
| /gcheck  | /ga c   |
| /grecent | /ga q   |

## Permissions
| Permission                    | Effect                                        |
| :---------                    | :-----                                        |
| `griefalert.messaging`        | Receive Alert messages                        |
| `griefalert.silent`           | Alerts originating from this player are muted |
| `griefalert.command`          | Access to **/ga**                             |
| `griefalert.command.check`    | Access to **/ga c**                           |
| `griefalert.command.info`     | Access to **/ga i**                           |
| `griefalert.command.show`     | Access to **/ga s**                           |
| `griefalert.command.query`    | Access to **/ga q**                           |
| `griefalert.command.fix`      | Access to **/ga f**                           |
| `griefalert.command.reload`   | Access to **/ga reload** and **/ga flush**    |
| `griefalert.command.profile`  | Access to **/ga p**                           |
| `griefalert.command.logs`     | Access to **/ga l**                           |
| `griefalert.command.rollback` | Access to **/ga rb** and **/ga rs**           |



## Definitions
- **Profile**: GriefAlert decides which alerts to create and send to staff members by checking if 
certain events--thrown either directly through Sponge or indirectly through Prism--match a specific Profile. 
The database of Profiles can be managed with **/ga p**. 
- **Alert**: Alerts are objects created when an event matches a Profile. 
When an Alert is created, the information within are then formatted in a variety of ways and sent to staff members.

## Contributors
- **PietElite**: Creator of Sponge GriefAlert and current model
- **darkdiplomat**: Worked on original model
- **BastetFurry**: Worked on original model
- **14mRh4X0r**: Worked on original model
