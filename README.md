# sqshy #

SqSHy is a simple SQL Shell, inspired by [sqsh](http://www.sqsh.org/),
[henplus](http://henplus.sourceforge.net/) and  `psql` (from Postgres).

This is currently work-in-progress, but pull requests are welcome. Many
of the existing features are more "proof-of-concept" than "production-
ready", so use at your own risk. APIs and features may change
unexpectedly as the functionality and implementation matures and
stabilizes.

## Features ##

*Current*

 * Support any JDBC-complaint database
 * Nice terminal handling ([jline2](https://github.com/jline/jline2))
 * Load settings (connection data) from user config file (`~/.sqshyrc`)
 * Control all functionality from user-defined variables which can be set from:
   * Built-in
   * User config
   * Per-connection config (in user config file)
   * Command line
   * In-app (`\set var value`)
 * Redirect or pipe output of all commands (`|`, `>`, `>>`)
 * Pretty tabular output
 * Auto-detect JDBC driver based on JDBC URL
 * Load JDBC drivers from outside Java's classpath

*Future*

 * Better formatting options for SQL output:
   * Pretty (current) with or without borders
     * Set max col width
     * Set individual col width (by name)
     * Truncate/wrap long cols
   * Tab/CSV/BCP
   * XML/JSON/HTML
   * Vertical
 * Schema inspection commands (list tables, views, other objects)
 * Save connection names (aliases) to user config
 * Save variables to user config
 * Command history
 * Tab-completion (not sure if this will be metadata-based like henplus,
   or a static list like sqsh)
 * `\e` command to edit in editor
 * Some method of allowing variable interpolation in queries
 * Some method of allowing bind variables in queries
 * Load/run scripts at startup
 * In-app help

## Command Line Arguments ##

Some command line arguments have short forms. If the argument does not
allow an argument, it can be combined with other short-form arguments.

Some arguments can be specified multiple times.

In general, the format of an argument is one of:

    -s            # short form
    -s arg        # short form with argument
    --long        # long form
    --long=arg    # long form with argument
    --long arg    # long form with argument

## Commands ##

Note: These are the current commands, which may change at any moment.

All commands can be piped to commands or have output redirected to a
file using standard shell syntax (`|`, `>`, `>>`).

**\set**

On its own, print all variables and values. With one argument, prints
the value of that variable. With two arguments, sets the value of the
first argument to the second. The second argument supports variable
interpolation.

**\echo**

Print a message. Takes any number of arguments, all of which support
variable interpolation.

**\disconnect**, **\reconnect**

They do what they say on the tin.

**\connect**

    \connect alias
    \connect url
    \connect url username password
    \connect url username password driver

Note if you want to set connection properties, at this point the only
way to do that is with a saved connection (loaded by using its alias) in
your local `.sqshyrc`.

**SQL**

Anything else is considered to be an SQL command. SQL commands are ended
by a semicolon ("`;`") (or the current value of the variable
`delimiter`) or "`\go`" at the beginning of a line (or the value of the
variable `gocmd`).

## Variables ##

**delimiter**

Default is "`;`". Ends an SQL statement.

**gocmd**

Default is "`\go`" at the beginning of a line. Ends an SQL statement.

**prompt**

Default is "`sql> `". The prompt.

**prompt2**

Default is "`> `". Continuation prompt for multi-line SQL statements.

### Builtin Variables ###

**env**

Environment variables (can be accessed like `env.PATH`). Read-only.

**sys**

Java System Properties (can be accessed like `sys.user.home`).

**connectionManager**

Details about the current connection. Read-only.

