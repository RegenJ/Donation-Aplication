# -*- coding: utf-8 -*-

from django.db import models

# Create your models here.

class Gathering(models.Model):
    owner = models.CharField(max_length=40)
    purpose = models.CharField(max_length=100)
    money_target = models.FloatField(default=None, blank=True, null=True)
    money_actual = models.FloatField(default=None, blank=True, null=True)
    start_date = models.DateTimeField(default=None, blank=True, null=True)
    end_date = models.DateTimeField(default=None, blank=True, null=True)

class Foundation(models.Model):
    owner = models.CharField(max_length=40)
    # todo: jakis prawny szajs

class Donation(models.Model):
    donor = models.CharField(max_length=40)
    receiver = models.CharField(max_length=40)
    value = models.FloatField(default=None, blank=True, null=True)
    date = models.DateField(default=None, blank=True, null=True)