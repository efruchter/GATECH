Questions to Answer
===================

Some questions I have which we'll need to answer to complete this project.

* Do we need a full-featured scanner and parser to parse the grammar input file?
* Is there a grammar which completely describes the grammar input file syntax?
  * i.e. is the grammar for the grammar input file regular?
* Are regexes embedded within a grammar to be interpreted at grammar-input-file-parsing-time?
  * Or should they be kept as strings for later interpretation?
* After we have a 'correct' parser, can we use it to create the scanner we previously created by-hand?
* The slides and Wikipedia are together pretty clear on how to take a token stream and confirm the program is valid, but... then what?
  * How to go from individual parser callbacks (eg. `term` in `rdp.Parser`) to an NFA
