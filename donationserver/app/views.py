# -*- coding: utf-8 -*-
import logging
from datetime import datetime
import random

from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.shortcuts import render
from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest
from django.core.exceptions import ObjectDoesNotExist
from django.views.decorators.csrf import csrf_exempt
from app.utils import *
from app.models import Gathering, UsersWalletInfo, Donation


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
            wif, address = create_new_wallet()
            wallet_info = UsersWalletInfo(owner=username, wallet_wif=wif, wallet_address=address)
            wallet_info.save()
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
        try:
            for e in list(Donation.objects.filter(donor=request.user)):
                logging.error(e)
            donated_gatherings = [Gathering.objects.get(pk=entry.gathering_id) for entry in
                                  list(Donation.objects.filter(donor=request.user))]
            donated_gatherings = list(set(donated_gatherings))
        except ObjectDoesNotExist:
            logging.error('WYSYPALO SIE !!')
            donated_gatherings = []
        wallet = list(UsersWalletInfo.objects.filter(owner=request.user))[0]
        balance = get_btc_wallet_balance(wallet_wif=wallet.wallet_wif)
        return render(request, 'home.html', {'donated_gatherings': donated_gatherings,
                                             'addrr': wallet.wallet_address,
                                             'balance': balance})
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
        wallet_address = list(UsersWalletInfo.objects.filter(owner=request.user))[0].wallet_address
        gathering = Gathering(owner=request.user,
                              purpose=purpose,
                              end_date=end_date,
                              wallet=wallet_address,
                              start_date=datetime.utcnow().strftime("%m. %d, %Y"),
                              money_target=float(target),
                              money_actual=0.0,
                              title=title,
                              percentage=0)
        gathering.save()
        return home_view(request)


@csrf_exempt
def query_gatherings(request):
    key = request.POST.get('search_key', None)
    all_entries = list(Gathering.objects.all())
    if key is None:
        best_matches = random.sample(all_entries, min(20, len(all_entries)))
    else:
        best_matches = find_best_matches(20, key, all_entries)
    return render(request, 'donate_others.html', {'searched_gatherings': best_matches})


@csrf_exempt
def paybtc(request):
    gathering_id = request.POST.get('gathering_id')
    receiver_address = Gathering.objects.get(pk=gathering_id).wallet
    return render(request, 'paybtc.html', {'id': gathering_id, 'receiver_address': receiver_address})


@csrf_exempt
def finalize_payment(request):
    gathering_id = int(request.POST.get('hidden_id'))
    amount = float(request.POST.get('amount'))
    gathering = Gathering.objects.get(pk=gathering_id)
    sender = list(UsersWalletInfo.objects.filter(owner=request.user))[0].wallet_wif
    transaction_id = pay_with_btc(sender, gathering.wallet, amount)
    gathering.money_actual += amount
    gathering.percentage = int((gathering.money_actual / gathering.money_target) * 100)
    gathering.save()
    donation = Donation(donor=request.user,
                        receiver=gathering.owner,
                        value=amount,
                        date=datetime.utcnow().strftime("%m. %d, %Y"),
                        transaction_id=transaction_id,
                        gathering_id=gathering_id)
    donation.save()
    return HttpResponse(
        'Payment finalized. You can track your payment here:\n https://testnet.blockchain.info/tx/{}'.format(
            transaction_id))
