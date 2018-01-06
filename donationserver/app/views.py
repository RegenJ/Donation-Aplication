# -*- coding: utf-8 -*-
import logging
from datetime import datetime
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.shortcuts import render
from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest
from django.core.exceptions import ObjectDoesNotExist
from django.views.decorators.csrf import csrf_exempt

from app.models import Gathering


@csrf_exempt
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
        username = request.POST.get('username')
        password = request.POST.get('password')
        email = request.POST.get('email')
        if _check_if_username_is_free(username):
            User.objects.create_user(username=username, email=email, password=password)
            return render(request, 'login.html')
        else:
            return HttpResponse('Username is not free. Choose another\n', status=405)
    else:
        return HttpResponseBadRequest('POST request expected\n')


@csrf_exempt
def login_view(request):
    return render(request, 'login.html')


@csrf_exempt
def logout_user(request):
    if request.user.is_authenticated:
        logout(request)
    return render(request, 'login.html')


@csrf_exempt
def login_user(request):
    logging.error('Request body ' + str(request.body))
    username = request.POST.get('username', None)
    logging.error('Username : ' + str(username))
    if username is None:
        logging.error('Username was None: ' + str(request.body))
        return HttpResponse('Please specify username\n', status=405)
    password = request.POST.get('password')
    logging.error('!!user: ' + username + '\t' + password)
    user = authenticate(request, username=username, password=password)
    if user is not None:
        login(request, user)
        logging.error('META: ' + str(request.META))
        return home_view(request)
    else:
        return HttpResponse('Bad credentials\n', status=405)


@csrf_exempt
def home_view(request):
    if request.user.is_authenticated:
        random_gatherings = list(Gathering.objects.all())
        return render(request, 'home.html', {'random_gatherings': random_gatherings})
    else:
        return render(request, 'login.html')


@csrf_exempt
def change_pass(request):
    if request.user.is_authenticated:
        return render(request, 'pass_change.html')
    else:
        return render(request, 'login.html')


@csrf_exempt
def gathering(request):
    if request.user.is_authenticated:
        return render(request, 'gathering.html')
    else:
        return render(request, 'login.html')


@csrf_exempt
def create_gathering(request):
    if request.user.is_authenticated and request.method == 'POST':
        purpose = request.POST.get('desc')
        target = request.POST.get('money')
        end_date = request.POST.get('date')
        title = request.POST.get('title')
        gathering = Gathering(owner=request.user,
                              purpose=purpose,
                              end_date=end_date,
                              start_date=datetime.utcnow().strftime("%M. %d, %Y"),
                              money_target=float(target),
                              money_actual=0.0,
                              title=title,
                              percentage=0)
        gathering.save()
        return home_view(request)
