# -*- coding: utf-8 -*-

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest
from django.core.exceptions import ObjectDoesNotExist
from django.views.decorators.csrf import csrf_exempt
from app.models import MyUser

def main_view(request):
    return render(request, 'main_view.html')

def _check_if_username_is_free(username):
    """
    Checks whether specified username already exists in db
    :param username:
    :return: True if exists otherwise False
    """
    try:
        MyUser.objects.get(username=username)
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
            MyUser.objects.create(username=username, password=password, email=email)
            return HttpResponse('Registration successful\n')
    else:
        return HttpResponseBadRequest('POST request expected\n')


