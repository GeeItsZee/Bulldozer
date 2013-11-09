Bulldozer
=========

Minecraft World Editing Plugin Primarily being developed for Bukkit

Shapes Implemented:
- Create a cube / rectangular prism:
  - /box -c [Block ID] [High Offset] [Low Offset]
  - /box -p [Block ID] [High Offset] [Low Offset]
- Create a border:
  - /border -c [Block ID] [High Offset] [Low Offset]
  - /border -p [Block ID] [High Offset] [Low Offset]
- Create a cylinder (Select one point):
  - /cyl -h [Block ID] [Radius] [Height] [Low Offset]
  - /cyl -f [Block ID] [Radius] [Height] [Low Offset]
- Create a sphere (Select one point):
  - /sph -h [Block ID] [Radius]
  - /sph -f [Block ID] [Radius]
- Create a cone (Select one point):
  - /cone -h [Block ID] [Radius of Base] [Height] [Low Offset]
  - /cone -f [Block ID] [Radius of Base] [Height] [Low Offset]

Tool Commands:
- /marker
  - Gives the player a tool to select blocks with
  - Once selected, the block changes to glass
- /clear
  - Removes all of the blocks in the current selection
  - All blocks that were glass change back to their original block
- /undo
  - Undos the last edit
- /undo all
  - All edits performed are undone
- /undo clear
  - The data used to store undo-ing are cleared (you will not be able to undo after this)
  - Warning: Use this only to make edits permanent. They cannot be undone.

