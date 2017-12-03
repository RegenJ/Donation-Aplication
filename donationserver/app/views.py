# -*- coding: utf-8 -*-
import logging
from datetime import datetime
from django.contrib.auth import authenticate, login
from django.contrib.auth.models import User
from django.shortcuts import render
from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest
from django.core.exceptions import ObjectDoesNotExist
from django.views.decorators.csrf import csrf_exempt

from app.models import Gathering


def _check_if_username_is_free(username):
    """
    Checks whether specified username already exists in db
    :param username:
    :return: True if exists otherwise False
    """
    try:
        User.objects.get(username=username)
        return False
    except ObjectDoesNotExist:
        return True

@csrf_exempt
def register_user(request):
    """
    Adds new user to db
    :param request:
    :return:
    """
    if request.method == 'POST':
        username = request.POST['username']
        password = request.POST['password']
        email = request.POST['email']
        if _check_if_username_is_free(username):
            User.objects.create_user(username=username, email=email, password=password)
            return render(request, 'login.html')
        else:
            return HttpResponse('Username is not free. Choose another\n', status=405)
    else:
        return HttpResponseBadRequest('POST request expected\n')



def login_view(request):
    return render(request, 'login.html')

def login_user(request):
    username = request.POST.get('username', None)
    if username is None:
        return HttpResponse('Please specify username\n', status=405)
    password = request.POST['password']
    logging.error('!!user: ' + username + '\t'+ password)
    user = authenticate(request, username=username, password=password)
    if user is not None:
        login(request, user)
        return home_view(request)
    else:
        return HttpResponse('Bad credentials\n', status=405)

def home_view(request):
    if request.user.is_authenticated:
        return render(request, 'home.html')
    else:
        return render(request, 'login.html')

def gathering(request):
    if request.user.is_authenticated:
        return render(request, 'gathering.html')
    else:
        return render(request, 'login.html')

def create_gathering(request):
    if request.user.is_authenticated and request.method=='POST':
        purpose = request.POST['desc']
        target = request.POST['money']
        end_date = request.POST['date']
        gathering = Gathering(owner=request.user,
                              purpose=purpose,
                              end_date=datetime.utcnow(),
                              start_date=datetime.utcnow(),
                              money_target=float(target),
                              money_actual=0.0)
        gathering.save()
        return render(request, 'home.html')