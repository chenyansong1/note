Postgres ERROR: could not open file for reading: Permission denied






解决的方式：

```tiki wiki
Assuming the psql command-line tool, you may use \copy instead of copy.

\copy opens the file and feeds the contents to the server, whereas copy tells the server the open the file itself and read it, which may be problematic permission-wise, or even impossible if client and server run on different machines with no file sharing in-between.

Under the hood, \copy is implemented as COPY FROM stdin and accepts the same options than the server-side COPY.
```
