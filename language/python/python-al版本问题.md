# Flask AttributeError: can't set attribute

参考：https://stackoverflow.com/questions/52724793/flask-attributeerror-cant-set-attribute

```shell
Traceback (most recent call last):
  File "<input>", line 1, in <module>
  File "<string>", line 4, in __init__
  File "C:\Users\Lenovo\PycharmProjects\FlaskProject\venv\lib\site-packages\sqlalchemy\orm\state.py", line 417, in _initialize_instance
    manager.dispatch.init_failure(self, args, kwargs)
  File "C:\Users\Lenovo\PycharmProjects\FlaskProject\venv\lib\site-packages\sqlalchemy\util\langhelpers.py", line 66, in __exit__
    compat.reraise(exc_type, exc_value, exc_tb)
  File "C:\Users\Lenovo\PycharmProjects\FlaskProject\venv\lib\site-packages\sqlalchemy\util\compat.py", line 249, in reraise
    raise value
  File "C:\Users\Lenovo\PycharmProjects\FlaskProject\venv\lib\site-packages\sqlalchemy\orm\state.py", line 414, in _initialize_instance
    return manager.original_init(*mixed[1:], **kwargs)
  File "C:\Users\Lenovo\PycharmProjects\FlaskProject\venv\lib\site-packages\sqlalchemy\ext\declarative\base.py", line 700, in _declarative_constructor
    setattr(self, k, kwargs[k])
AttributeError: can't set attribute
```



解决方式：

I have also encountered the same issue today. Most likely it is an issue with `SQLAlchemy` which is a dependancy of `flask-sqlalchemy`. I compared the version I used in a recent project and the current version and found out there is an update to `SQLAlchemy==1.4.0`.

To check the version, I did:

```py
$ pip3 freeze
```

To revert to the previous version, I first uninstalled the current `SQLAlchemy==1.4.0` and reinstalled the previous version `SQLAlchemy==1.3.23`

```py
# 替换版本
$ pip3 uninstall SQLAlchemy==1.4.0 # my current version
$ pip3 install SQLAlchemy==1.3.23 # previous working version
```

Update your requirements:

```py
# 重新加载
$ pip3 freeze > requirements.txt
```

