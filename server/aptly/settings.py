# Django settings for aptly project.

import secrets

DEBUG = False
TEMPLATE_DEBUG = DEBUG

ADMINS = secrets.ADMINS
MANAGERS = ADMINS

DATABASE_ENGINE = secrets.DATABASE_ENGINE
DATABASE_NAME = secrets.DATABASE_NAME
DATABASE_USER = secrets.DATABASE_USER
DATABASE_PASSWORD = secrets.DATABASE_PASSWORD
DATABASE_HOST = secrets.DATABASE_HOST
DATABASE_PORT = secrets.DATABASE_PORT

TIME_ZONE = 'Europe/Stockholm'

LANGUAGE_CODE = 'en-us'

SITE_ID = 1

USE_I18N = True

MEDIA_ROOT = ''
MEDIA_URL = ''
ADMIN_MEDIA_PREFIX = '/media/'

SECRET_KEY = secrets.SECRET_KEY

TEMPLATE_LOADERS = (
    'django.template.loaders.filesystem.load_template_source',
    'django.template.loaders.app_directories.load_template_source',
)

MIDDLEWARE_CLASSES = (
    'django.middleware.common.CommonMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
)

ROOT_URLCONF = 'aptly.urls'

TEMPLATE_DIRS = (
)

INSTALLED_APPS = (
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.sites',
)
