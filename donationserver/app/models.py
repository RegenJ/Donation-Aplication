# -*- coding: utf-8 -*-

from django.db import models


# Create your models here.

class Gathering(models.Model):
    owner = models.CharField(max_length=40)
    title = models.CharField(max_length=100)
    wallet = models.CharField(max_length=100)
    purpose = models.CharField(max_length=1000)
    money_target = models.FloatField(default=None, blank=True, null=True)
    money_actual = models.FloatField(default=None, blank=True, null=True)
    start_date = models.CharField(max_length=50)
    end_date = models.CharField(max_length=50)
    percentage = models.IntegerField(default=0)


class UsersWalletInfo(models.Model):
    owner = models.CharField(max_length=40)
    wallet_wif = models.CharField(max_length=100)
    wallet_address = models.CharField(max_length=100)


class Foundation(models.Model):
    owner = models.CharField(max_length=40)
    # todo: jakis prawny szajs


class Donation(models.Model):
    donor = models.CharField(max_length=40)
    receiver = models.CharField(max_length=40)
    value = models.FloatField(default=None, blank=True, null=True)
    date = models.DateField(default=None, blank=True, null=True)
