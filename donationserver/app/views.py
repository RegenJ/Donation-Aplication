# -*- coding: utf-8 -*-

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest
from django.contrib.auth.models import User
from django.core.exceptions import ObjectDoesNotExist
from django.views.decorators.csrf import csrf_exempt


def _check_if_username_is_free(username):
    """
    Checks whether specified username already exists in db
    :param username:
    :return: True if exists otherwise False
    """
    try:
        User.objects.get(username=username)
        return True
    except ObjectDoesNotExist:
        return False


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
            return HttpResponse('Username is not free. Choose another\n', status=405)
        else:
            User.objects.create_user(username, email, password)
            return HttpResponse('Registration successful\n')
    else:
        return HttpResponseBadRequest('POST request expected\n')


