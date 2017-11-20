# -*- coding: utf-8 -*-

from django.db import models

# Create your models here.

class MyUser(models.Model):
    username = models.CharField(max_length=30)
    password = models.CharField(max_length=30)
    email = models.CharField(max_length=40)