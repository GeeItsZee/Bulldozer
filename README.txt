Bulldozer
=========

Minecraft World Editing Plugin Primarily being developed for Bukkit

General Flag Explaination:
- The flag "-c" is for chunk-based edits. The selected chunks will be edited.
- The flag "-p" is for point-based edits. The blocks between the two farthest blocks will be edited.
- The flag "-f" is for filled shapes. The shape created will be filled with blocks. Do these edits AWAY from the selection.
- The flag "-h" is for hollow shapes. The shape created will be hollow / empty on the inside. 

General Value Explaination:
- Block ID: A value from 0 to 173 of the block desired.
- High Offset: The extra height to add to an edit. If you select two blocks on the same height and have a high offset of 10, an edit of 10 high will be created.
- Low Offset: The extra depth to add to an edit. If you select two blocks on the same height and have a low offset of 10, an edit of 10 deep will be created.
- Height: A value for how high the shape should be.
- Radius: The radius of the circle (the base for cylinders and cones, the size of spheres).

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
- Replace (Area of effect like /box):
  - /replace -c [Original Block ID] [Desired Block ID] [High Offset] [Low Offset]
  - /replace -p [Original Block ID] [Desired Block ID] [High Offset] [Low Offset]

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

