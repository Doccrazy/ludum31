This is my entry for Ludum Dare #31. Have fun, but don't expect too much.

Compiling
=========

The game requires Java 8 and Gradle to compile. All other dependencies should be
downloaded automatically by Gradle.  
Also, my common framework [ludum-shared](https://bitbucket.org/d0ccrazy/ludum-shared ) needs to be available as a submodule.
I suggest to symlink it into the main repository.

Windows:
    mklink /D ludum-shared ..\ludum-shared

Linux:
    ln -s ../ludum-shared ludum-shared

After this setup, compile the "desktop" module using `gradle dist` or run it directly using `gradle run`.
