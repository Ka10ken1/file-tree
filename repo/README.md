# File Tree
**Please be aware the path on Unix enviroment is CASE SENSITIVE.** For example path usr/path/custom/example.txt 
is equals to usr/Path/Custom/Example.txt on Windows but NOT on UNIX environment.

## Description
Build a String representation of directory hierarchy under a given path  

## Details
1. Implement the `tree` method in [FileTreeImpl](src/main/java/com/efimchick/ifmo/io/filetree/FileTreeImpl.java) class.
2. An input parameter is a path.
3. If a given path *does not exist*, return an empty Optional.
4. If a given path *refers to a file*, return a String with its name and size like this: 

    
    some-file.txt 128 bytes
    
5. If a given path *refers to a directory*, return a String with its name, total size and its full hierarchy:


    some-dir 100 bytes
    ├─ some-inner-dir 50 bytes
    │  ├─ some-file.txt 20 bytes    
    │  └─ some-other-file.txt 30 bytes
    └─ some-one-more-file.txt 50 bytes
    
- Use pseudo-graphic characters to format the output.
- Compute the size of a directory as a sum of all its contents.
- Sort the contents in following way:
    - Directories should go first.
    - Directories and files are sorted in lexicographic order (case-insensitive).
