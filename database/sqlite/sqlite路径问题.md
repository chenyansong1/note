### SQLite

SQLite connects to file-based databases, using the Python built-in module `sqlite3` by default.

As SQLite connects to local files, the URL format is slightly different. The “file” portion of the URL is the filename of the database. For a relative file path, this requires three slashes:

```
# sqlite://<nohostname>/<path>
# where <path> is relative:
engine = create_engine('sqlite:///foo.db')
```

And for an absolute file path, the three slashes are followed by the absolute path:

```
# Unix/Mac - 4 initial slashes in total
engine = create_engine('sqlite:////absolute/path/to/foo.db')

# Windows
engine = create_engine('sqlite:///C:\\path\\to\\foo.db')

#使用 r 就不用转义
# Windows alternative using raw string
engine = create_engine(r'sqlite:///C:\path\to\foo.db')
```

To use a SQLite `:memory:` database, specify an empty URL:

```
engine = create_engine('sqlite://')
```

