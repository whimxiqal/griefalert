# 1.4.0
- Profiles may now be made `translucent`, meaning GriefAlert will attempt to determine
  if an Alert is thought to be caused by grief and, if not, silence the Alert.
- GA Tool is now a stick

# 1.3.1-2
- Fixed concurrency problems with officer inspection history
- Turned `since` flag into `after` flag on applier commands
- Officers now teleport to the block of grief instead of the player by default
- Fixed the `check` command flags
- Added `enabletool` command for administrators
- Show formatted name in Alert messages using Hermes service
- Bump Prism dependency to 3.0.2
- Added GriefAlert tool to staff panel
- Destroy GriefAlert tool upon any inventory action

# 1.3.0
- introduced the GriefAlert tool with `/ga tool <griefer>`

# 1.2.6
- added a configurable timeout for alerts with repeated profiles to reduce alert spam
- changed alert silencing behavior to silence non-consecutive repeated alerts
- added low-level command functionality
  - a staff member can inspect an alert by going directly to the griefed block location
- added flag descriptions to the help menus

# 1.2.5
- changed clickable-command color to light-purple
- made inspection return timeout configurable
- made the world configurable in alerts to show dimension instead of world name
- made missing configuration nodes show up in the config file if not found
- added emerald block and emerald ore to default profiles

# < 1.2.5
Untracked